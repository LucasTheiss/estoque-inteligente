package br.com.estoque.service;

import br.com.estoque.domain.Movimentacao;

/** Template Method: organiza as etapas comuns das operações de estoque. */
public abstract class OperacaoEstoque {
    public final Movimentacao executar(String sku, int quantidade) {
        validar(sku, quantidade);
        Movimentacao resultado = aplicar(sku, quantidade);
        registrar(resultado);
        return resultado;
    }

    protected void validar(String sku, int quantidade) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU obrigatório");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
    }

    protected abstract Movimentacao aplicar(String sku, int quantidade);
    protected abstract void registrar(Movimentacao movimentacao);
}
