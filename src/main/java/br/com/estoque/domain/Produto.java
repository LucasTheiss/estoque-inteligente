package br.com.estoque.domain;

import java.math.BigDecimal;

public record Produto(String sku, String nome, int quantidade, int estoqueMinimo, BigDecimal custoUnitario) {
    public Produto {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU obrigatório");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome obrigatório");
        if (quantidade < 0 || estoqueMinimo < 0) throw new IllegalArgumentException("Estoque não pode ser negativo");
        if (custoUnitario == null || custoUnitario.signum() < 0) throw new IllegalArgumentException("Custo inválido");
    }
}
