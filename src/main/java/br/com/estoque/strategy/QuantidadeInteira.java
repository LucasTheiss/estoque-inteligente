package br.com.estoque.strategy;
import java.math.BigDecimal;
public final class QuantidadeInteira implements Quantidade {
    public BigDecimal validar(String valor) { return Quantidade.decimal(valor,0); }
}
