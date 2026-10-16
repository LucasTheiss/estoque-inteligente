import br.com.estoque.service.AjusteEstoque;
import br.com.estoque.service.EntradaEstoque;
import br.com.estoque.service.SaidaEstoque;
import br.com.estoque.strategy.CalculoEstoque;
import br.com.estoque.strategy.Quantidade;
import br.com.estoque.strategy.QuantidadeFracionada;
import br.com.estoque.strategy.QuantidadeInteira;
import br.com.estoque.strategy.QuantidadeMonetaria;
import java.math.BigDecimal;

/** Consumidor independente: compila apenas com o JAR, sem Spring ou banco. */
public class ConsumidorEstoque {
    public static void main(String[] args) {
        Quantidade inteiros = new QuantidadeInteira();
        Quantidade pesos = new QuantidadeFracionada();
        Quantidade precos = new QuantidadeMonetaria();
        CalculoEstoque entrada = new EntradaEstoque();
        CalculoEstoque saida = new SaidaEstoque();
        CalculoEstoque ajuste = new AjusteEstoque();
        BigDecimal saldo = entrada.executar(BigDecimal.ZERO, inteiros.validar("10"));
        assert saldo.compareTo(BigDecimal.TEN) == 0;
        saldo = saida.executar(saldo, inteiros.validar("3"));
        assert saldo.compareTo(new BigDecimal("7")) == 0;
        saldo = ajuste.executar(saldo, inteiros.validar("8"));
        assert saldo.compareTo(new BigDecimal("8")) == 0;
        assert ajuste.executar(saldo, BigDecimal.ZERO).signum() == 0;
        assert pesos.validar("0.125").compareTo(new BigDecimal("0.125")) == 0;
        assert precos.validar("12.50").compareTo(new BigDecimal("12.50")) == 0;
        rejeita(() -> inteiros.validar("0.5"));
        rejeita(() -> pesos.validar("0.0001"));
        rejeita(() -> precos.validar("12.501"));
        rejeita(() -> saida.executar(BigDecimal.ONE, BigDecimal.TEN));
        rejeita(() -> ajuste.executar(BigDecimal.ONE, BigDecimal.ONE));
        rejeita(() -> entrada.executar(BigDecimal.ONE, new BigDecimal("-1")));
        System.out.println("Reuso verificado: 0 -> 10 -> 7 -> 8; precisao e rejeicoes preservadas.");
    }

    private static void rejeita(Runnable operacao) {
        try {
            operacao.run();
        } catch (IllegalArgumentException esperada) {
            return;
        }
        throw new AssertionError("A operacao invalida deveria ter sido rejeitada.");
    }
}
