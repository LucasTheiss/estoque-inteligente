package br.com.estoque.strategy;
import java.math.BigDecimal;
public final class QuantidadeFracionada implements Quantidade {
    public BigDecimal validar(String valor) { return Quantidade.decimal(valor,3); }
}
