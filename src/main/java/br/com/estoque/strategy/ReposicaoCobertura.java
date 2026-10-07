package br.com.estoque.strategy;

import br.com.estoque.domain.OrdemReposicao;
import br.com.estoque.domain.Produto;
import java.util.Optional;

public class ReposicaoCobertura implements PoliticaReposicao {
    private final int consumoDiario;
    private final int diasCobertura;

    public ReposicaoCobertura(int consumoDiario, int diasCobertura) {
        if (consumoDiario <= 0 || diasCobertura <= 0) throw new IllegalArgumentException("Consumo e cobertura devem ser positivos");
        this.consumoDiario = consumoDiario;
        this.diasCobertura = diasCobertura;
    }

    @Override public Optional<OrdemReposicao> calcular(Produto p) {
        int alvo = Math.multiplyExact(consumoDiario, diasCobertura);
        return p.quantidade() < p.estoqueMinimo()
                ? Optional.of(new OrdemReposicao(p.sku(), Math.max(1, alvo - p.quantidade()), "Reposição para cobertura"))
                : Optional.empty();
    }
}
