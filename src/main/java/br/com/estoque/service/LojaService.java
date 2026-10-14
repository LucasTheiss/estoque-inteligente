package br.com.estoque.service;

import br.com.estoque.dto.LojaForm;
import br.com.estoque.model.Loja;
import br.com.estoque.repository.*;
import java.util.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LojaService {
    private final LojaRepository lojas;
    private final UsuarioRepository usuarios;
    private final ProdutoRepository produtos;
    private final PermissaoService permissoes;
    private final AuditoriaRepository auditoria;
    private final Acesso acesso;
    private final PasswordEncoder senhas;
    public LojaService(LojaRepository lojas,UsuarioRepository usuarios,ProdutoRepository produtos,PermissaoService permissoes,AuditoriaRepository auditoria,Acesso acesso,PasswordEncoder senhas) {
        this.lojas=lojas;this.usuarios=usuarios;this.produtos=produtos;this.permissoes=permissoes;this.auditoria=auditoria;this.acesso=acesso;this.senhas=senhas;
    }
    public List<Loja> disponiveisLogin() { return lojas.listar(); }
    public List<Loja> listar() { acesso.global();return lojas.listar(); }
    public Loja contexto(long id) { acesso.permissoes(id);return lojas.buscar(id); }
    public Loja editar(long id) { acesso.global();return lojas.buscar(id); }
    public long selecionar(long id) {
        acesso.global();lojas.buscar(id);
        auditoria.registrar(id,acesso.usuario().id(),"loja.selecionar","Acesso global à loja");
        return id;
    }
    @Transactional public void salvar(LojaForm f) {
        acesso.global();
        String slug=Campos.texto(f.slug(),"identificador",true),nome=Campos.texto(f.name(),"nome",true);
        if(!slug.matches("[a-z0-9-]{1,40}") || slug.equals("global"))throw new IllegalArgumentException("Identificador da loja inválido ou reservado.");
        long id;
        if(f.id()!=null) {
            id=f.id();lojas.bloquear(id);
            if(!f.fractional() && produtos.temFracionados(id))throw new IllegalArgumentException("Há produtos fracionados ativos nesta loja.");
            lojas.atualizar(id,nome,f.iot(),f.enterprise(),f.fractional());
        } else {
            Campos.senha(f.password());
            id=lojas.criar(slug,nome,f.iot(),f.enterprise(),f.fractional());
            permissoes.iniciais(id);
            usuarios.criar(id,Campos.texto(f.username(),"usuário",true),senhas.encode(f.password()),"admin");
        }
        auditoria.registrar(id,acesso.usuario().id(),"loja.salvar",nome);
    }
}
