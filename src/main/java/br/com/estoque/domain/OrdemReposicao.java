package br.com.estoque.domain;

public record OrdemReposicao(String sku, int quantidade, String motivo) {
    public OrdemReposicao {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU obrigatório");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        if (motivo == null || motivo.isBlank()) throw new IllegalArgumentException("Motivo obrigatório");
    }
}
