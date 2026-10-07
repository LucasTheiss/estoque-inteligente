package br.com.estoque.domain;

import java.time.Instant;

public record Movimentacao(String sku, int quantidade, Tipo tipo, Instant realizadaEm) {
    public enum Tipo { ENTRADA, SAIDA, AJUSTE }

    public Movimentacao {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU obrigatório");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        if (tipo == null) throw new IllegalArgumentException("Tipo obrigatório");
        if (realizadaEm == null) realizadaEm = Instant.now();
    }
}
