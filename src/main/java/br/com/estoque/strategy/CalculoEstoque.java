package br.com.estoque.strategy;

import java.math.BigDecimal;

/** Strategy: contrato das operações selecionadas pelo tipo de movimentação. */
public interface CalculoEstoque {
    BigDecimal executar(BigDecimal saldo, BigDecimal quantidade);
}
