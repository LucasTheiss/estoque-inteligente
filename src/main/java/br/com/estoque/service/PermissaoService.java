package br.com.estoque.service;

import br.com.estoque.repository.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissaoService {
    private final PermissaoRepository permissoes;
    private final AuditoriaRepository auditoria;
    private final Acesso acesso;

    public PermissaoService(
            PermissaoRepository permissoes, AuditoriaRepository auditoria, Acesso acesso) {
        this.permissoes = permissoes;
        this.auditoria = auditoria;
        this.acesso = acesso;
    }

    @Transactional
    public void iniciais(long loja) {
        for (String perfil : Acesso.PERFIS)
            for (String permissao : Acesso.PERMISSOES.keySet()) {
                boolean permite =
                        switch (perfil) {
                            case "vendedor" -> Set.of("consultar", "saida").contains(permissao);
                            case "estoquista" ->
                                    !Set.of("usuarios", "iot", "rbac", "relatorios")
                                            .contains(permissao);
                            case "gerente" -> !Set.of("iot", "rbac").contains(permissao);
                            default -> true;
                        };
                if (permite) permissoes.conceder(loja, perfil, permissao);
            }
    }

    public Set<String> matriz(long loja) {
        acesso.exigir(loja, "usuarios");
        return permissoes.listar(loja).stream()
                .map(p -> p.role() + ":" + p.permission())
                .collect(Collectors.toSet());
    }

    @Transactional
    public void atualizar(long loja, Set<String> campos) {
        acesso.exigir(loja, "rbac");
        if (!acesso.usuario().superadmin() && !acesso.usuario().perfil().equals("admin"))
            throw new IllegalArgumentException("Somente administradores alteram permissões.");
        for (String perfil : List.of("vendedor", "estoquista", "gerente")) {
            permissoes.limparPerfil(loja, perfil);
            for (String p : Acesso.PERMISSOES.keySet())
                if (!p.equals("rbac") && campos.contains(perfil + ":" + p))
                    permissoes.conceder(loja, perfil, p);
        }
        auditoria.registrar(loja, acesso.usuario().id(), "permissoes.editar", "Matriz atualizada");
    }
}
