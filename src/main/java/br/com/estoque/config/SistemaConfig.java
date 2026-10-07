package br.com.estoque.config;

import java.util.Map;

/** Singleton: configurações compartilhadas do protótipo. */
public final class SistemaConfig {
    private static final SistemaConfig INSTANCE = new SistemaConfig();
    private final Map<String, String> valores = Map.of(
            "sistema.nome", "Estoque Inteligente",
            "estoque.alerta-percentual", "20"
    );

    private SistemaConfig() {}

    public static SistemaConfig getInstance() { return INSTANCE; }

    public String get(String chave) {
        String valor = valores.get(chave);
        if (valor == null) throw new IllegalArgumentException("Configuração inexistente: " + chave);
        return valor;
    }
}
