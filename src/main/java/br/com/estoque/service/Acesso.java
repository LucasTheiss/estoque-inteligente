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
    public static final List<String> PERFIS =
            br.com.estoque.config.CatalogoPermissoes.getInstance().perfis();
    public static final Map<String, String> PERMISSOES =
            br.com.estoque.config.CatalogoPermissoes.getInstance().permissoes();
    private final UsuarioRepository usuarios;
    private final PermissaoRepository permissoes;
    private final LojaRepository lojas;

    public Acesso(
            UsuarioRepository usuarios, PermissaoRepository permissoes, LojaRepository lojas) {
        this.usuarios = usuarios;
        this.permissoes = permissoes;
        this.lojas = lojas;
    }

    public Usuario usuario() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioAutenticado principal))
            throw new AccessDeniedException("Entre na sua conta.");
        var atual =
                usuarios.porId(principal.usuario().id())
                        .orElseThrow(
                                () ->
                                        new AccessDeniedException(
                                                "Sessão revogada. Entre novamente."));
        if (!atual.ativo() || atual.versao() != principal.usuario().versao())
            throw new AccessDeniedException("Sessão revogada. Entre novamente.");
        if (!atual.superadmin() && !lojas.ativa(atual.lojaId()))
            throw new AccessDeniedException("Loja desativada. Entre em outra loja.");
        return atual;
    }

    public long loja(HttpSession session) {
        var usuario = usuario();
        Long id = usuario.superadmin() ? (Long) session.getAttribute("loja") : usuario.lojaId();
        if (id == null) throw new AccessDeniedException("Selecione uma loja no painel global.");
        return id;
    }

    public Set<String> permissoes(long loja) {
        var usuario = usuario();
        if (!lojas.ativa(loja)) throw new AccessDeniedException("Loja desativada ou inexistente.");
        if (usuario.superadmin()) return PERMISSOES.keySet();
        if (!Objects.equals(usuario.lojaId(), loja))
            throw new AccessDeniedException("Loja não autorizada.");
        return permissoes.doPerfil(loja, usuario.perfil());
    }

    public void exigir(long loja, String permissao) {
        if (!permissoes(loja).contains(permissao))
            throw new AccessDeniedException("Seu perfil não permite esta ação.");
    }

    public void global() {
        if (!usuario().superadmin())
            throw new AccessDeniedException("Acesso exclusivo do superadministrador.");
    }

    public void recurso(long loja, String recurso) {
        var tenant = lojas.compartilhar(loja);
        boolean habilitado =
                switch (recurso) {
                    case "iot" -> tenant.iot();
                    case "enterprise" -> tenant.enterprise();
                    case "fractional" -> tenant.fractional();
                    default -> throw new IllegalArgumentException("Recurso inválido.");
                };
        if (!habilitado) throw new AccessDeniedException("Recurso não habilitado nesta loja.");
    }
}
