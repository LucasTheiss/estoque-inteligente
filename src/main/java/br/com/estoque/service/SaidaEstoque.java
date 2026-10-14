package br.com.estoque.service;
import java.math.BigDecimal;
public final class SaidaEstoque extends OperacaoEstoque {
    @Override protected BigDecimal calcular(BigDecimal saldo,BigDecimal quantidade) { return saldo.subtract(quantidade); }
}
