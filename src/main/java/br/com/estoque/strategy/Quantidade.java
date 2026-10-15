package br.com.estoque.strategy;

import java.math.BigDecimal;

public interface Quantidade {
    BigDecimal validar(String valor);

    static BigDecimal decimal(String valor, int casas) {
        try {
            BigDecimal n = new BigDecimal(valor);
            if (n.signum() < 0
                    || n.stripTrailingZeros().scale() > casas
                    || n.compareTo(new BigDecimal("1000000000000")) >= 0)
                throw new NumberFormatException();
            return n;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Informe um valor não negativo com até " + casas + " casas decimais.");
        }
    }
}
