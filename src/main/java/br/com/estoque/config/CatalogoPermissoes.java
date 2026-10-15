package br.com.estoque.config;

import java.util.*;

/** Singleton: catálogo imutável compartilhado pela autorização e pelas telas. */
public final class CatalogoPermissoes {
    private static final CatalogoPermissoes INSTANCE = new CatalogoPermissoes();
    private final List<String> perfis = List.of("vendedor", "estoquista", "gerente", "admin");
    private final Map<String, String> permissoes;

    private CatalogoPermissoes() {
        var p = new LinkedHashMap<String, String>();
        p.put("consultar", "Consultar estoque");
        p.put("saida", "Registrar saídas");
        p.put("entrada", "Registrar entradas");
        p.put("cadastros", "Editar cadastros");
        p.put("ajuste", "Ajustar saldo");
        p.put("estorno", "Estornar");
        p.put("historico", "Consultar histórico");
        p.put("usuarios", "Gerenciar usuários");
        p.put("iot", "Configurar IoT");
        p.put("relatorios", "Relatórios e exportação");
        p.put("rbac", "Alterar permissões");
        permissoes = Collections.unmodifiableMap(p);
    }

    public static CatalogoPermissoes getInstance() {
        return INSTANCE;
    }

    public List<String> perfis() {
        return perfis;
    }

    public Map<String, String> permissoes() {
        return permissoes;
    }
}
