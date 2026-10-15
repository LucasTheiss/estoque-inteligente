package br.com.estoque.config;

import java.util.Map;

/** Singleton: identidade compartilhada pelas telas da aplicação. */
public final class SistemaConfig {
    private static final SistemaConfig INSTANCE = new SistemaConfig();
    private final Map<String, String> valores = Map.of("sistema.nome", "Estoque Inteligente");

    private SistemaConfig() {}

    public static SistemaConfig getInstance() {
        return INSTANCE;
    }

    public String get(String chave) {
        String valor = valores.get(chave);
        if (valor == null) throw new IllegalArgumentException("Configuração inexistente: " + chave);
        return valor;
    }
}
