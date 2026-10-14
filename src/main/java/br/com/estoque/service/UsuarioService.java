package br.com.estoque.service;

import br.com.estoque.dto.UsuarioForm;
import br.com.estoque.model.Usuario;
import br.com.estoque.repository.*;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final LojaRepository lojas;
    private final AuditoriaRepository auditoria;
    private final Acesso acesso;
    private final PasswordEncoder senhas;
    public UsuarioService(UsuarioRepository usuarios,LojaRepository lojas,AuditoriaRepository auditoria,Acesso acesso,PasswordEncoder senhas) {
        this.usuarios=usuarios;this.lojas=lojas;this.auditoria=auditoria;this.acesso=acesso;this.senhas=senhas;
    }
    public List<Usuario> listar(long loja) { acesso.exigir(loja,"usuarios");return usuarios.listar(loja); }
    public Usuario buscar(long loja,long id) { acesso.exigir(loja,"usuarios");return usuarios.buscar(loja,id); }
    @Transactional public void salvar(long loja,UsuarioForm f) {
        acesso.exigir(loja,"usuarios");
        var ator=acesso.usuario();lojas.bloquear(loja);
        String perfil=Campos.texto(f.role(),"perfil",true),nome=Campos.texto(f.username(),"usuário",true);
        String senha=f.password()==null?"":f.password();
        if(!Acesso.PERFIS.contains(perfil))throw new IllegalArgumentException("Perfil inválido.");
        var antigo=f.id()==null?null:usuarios.buscar(loja,f.id());
        boolean excluir="delete".equals(f.action());
        if(!ator.superadmin() && !ator.perfil().equals("admin") && (perfil.equals("admin") || (antigo!=null && antigo.perfil().equals("admin"))))
            throw new IllegalArgumentException("Somente administradores podem gerenciar esse perfil.");
        if(f.id()!=null && f.id().equals(ator.id()) && (excluir || !perfil.equals(ator.perfil())))throw new IllegalArgumentException("Não exclua nem rebaixe seu próprio perfil.");
        if(antigo!=null && antigo.perfil().equals("admin") && (excluir || !perfil.equals("admin")) && usuarios.administradores(loja)<=1)
            throw new IllegalArgumentException("Mantenha um administrador ativo por loja.");
        if(excluir) {
            if(antigo==null)throw new IllegalArgumentException("Selecione o usuário para excluir.");
            usuarios.desativar(loja,f.id());
        } else {
            if(antigo==null || !senha.isBlank())Campos.senha(senha);
            if(antigo==null)usuarios.criar(loja,nome,senhas.encode(senha),perfil);
            else usuarios.atualizar(loja,f.id(),nome,perfil,senha.isBlank()?antigo.senha():senhas.encode(senha));
        }
        auditoria.registrar(loja,ator.id(),"usuario."+(excluir?"excluir":"salvar"),nome+"; "+perfil);
    }
}
