package br.com.estoque.service;

import java.math.BigDecimal;

public final class AjusteEstoque extends OperacaoEstoque {
    @Override
    protected BigDecimal calcular(BigDecimal saldo, BigDecimal quantidade) {
        return quantidade;
    }
}
