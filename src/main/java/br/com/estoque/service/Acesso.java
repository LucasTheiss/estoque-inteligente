package br.com.estoque.service;

import br.com.estoque.model.Usuario;
import br.com.estoque.repository.*;
import br.com.estoque.security.UsuarioAutenticado;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class Acesso {
    public static final List<String> PERFIS=List.of("vendedor","estoquista","gerente","admin");
    public static final Map<String,String> PERMISSOES;
    static {
        var p=new LinkedHashMap<String,String>();
        p.put("consultar","Consultar estoque");p.put("saida","Registrar saídas");p.put("entrada","Registrar entradas");
        p.put("cadastros","Editar cadastros");p.put("ajuste","Ajustar saldo");p.put("estorno","Estornar");
        p.put("historico","Consultar histórico");p.put("usuarios","Gerenciar usuários");p.put("iot","Configurar IoT");
        p.put("relatorios","Relatórios e exportação");p.put("rbac","Alterar permissões");
        PERMISSOES=Collections.unmodifiableMap(p);
    }
    private final UsuarioRepository usuarios;
    private final PermissaoRepository permissoes;
    private final LojaRepository lojas;
    public Acesso(UsuarioRepository usuarios,PermissaoRepository permissoes,LojaRepository lojas) {
        this.usuarios=usuarios;this.permissoes=permissoes;this.lojas=lojas;
    }
    public Usuario usuario() {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null || !(auth.getPrincipal() instanceof UsuarioAutenticado principal))throw new AccessDeniedException("Entre na sua conta.");
        var atual=usuarios.porId(principal.usuario().id()).orElseThrow(()->new AccessDeniedException("Sessão revogada. Entre novamente."));
        if(!atual.ativo() || atual.versao()!=principal.usuario().versao())throw new AccessDeniedException("Sessão revogada. Entre novamente.");
        return atual;
    }
    public long loja(HttpSession session) {
        var usuario=usuario();
        Long id=usuario.superadmin()?(Long)session.getAttribute("loja"):usuario.lojaId();
        if(id==null)throw new AccessDeniedException("Selecione uma loja no painel global.");
        return id;
    }
    public Set<String> permissoes(long loja) {
        var usuario=usuario();
        if(usuario.superadmin())return PERMISSOES.keySet();
        if(!Objects.equals(usuario.lojaId(),loja))throw new AccessDeniedException("Loja não autorizada.");
        return permissoes.doPerfil(loja,usuario.perfil());
    }
    public void exigir(long loja,String permissao) {
        if(!permissoes(loja).contains(permissao))throw new AccessDeniedException("Seu perfil não permite esta ação.");
    }
    public void global() { if(!usuario().superadmin())throw new AccessDeniedException("Acesso exclusivo do superadministrador."); }
    public void recurso(long loja,String recurso) {
        var tenant=lojas.compartilhar(loja);
        boolean habilitado=switch(recurso) {case "iot"->tenant.iot();case "enterprise"->tenant.enterprise();case "fractional"->tenant.fractional();default->throw new IllegalArgumentException("Recurso inválido.");};
        if(!habilitado)throw new AccessDeniedException("Recurso não habilitado nesta loja.");
    }
}
