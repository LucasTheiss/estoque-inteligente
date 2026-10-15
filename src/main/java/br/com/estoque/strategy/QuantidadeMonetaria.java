package br.com.estoque.strategy;

import java.math.BigDecimal;

/** Precisão de preço, sem introduzir outra unidade de estoque. */
public final class QuantidadeMonetaria implements Quantidade {
    @Override public BigDecimal validar(String valor) { return Quantidade.decimal(valor,2); }
}
