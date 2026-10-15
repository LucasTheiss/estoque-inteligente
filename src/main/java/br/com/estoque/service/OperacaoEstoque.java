package br.com.estoque.service;
import java.math.BigDecimal;
/** Template Method: cálculo especializado, invariantes comuns de saldo. */
public abstract class OperacaoEstoque implements br.com.estoque.strategy.CalculoEstoque {
    public final BigDecimal executar(BigDecimal saldo, BigDecimal quantidade) {
        if (saldo == null || quantidade == null || saldo.signum()<0 || quantidade.signum()<0)
            throw new IllegalArgumentException("Quantidade inválida.");
        BigDecimal finalSaldo=calcular(saldo,quantidade);
        if(finalSaldo.signum()<0) throw new IllegalArgumentException("Estoque insuficiente nesta posição.");
        if(finalSaldo.compareTo(saldo)==0) throw new IllegalArgumentException("A operação não altera o saldo.");
        return finalSaldo;
    }
    protected abstract BigDecimal calcular(BigDecimal saldo,BigDecimal quantidade);
}
