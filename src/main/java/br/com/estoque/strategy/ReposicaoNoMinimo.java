package br.com.estoque.strategy;

import br.com.estoque.domain.OrdemReposicao;
import br.com.estoque.domain.Produto;
import java.util.Optional;

public class ReposicaoNoMinimo implements PoliticaReposicao {
    @Override public Optional<OrdemReposicao> calcular(Produto p) {
        return p.quantidade() < p.estoqueMinimo()
                ? Optional.of(new OrdemReposicao(p.sku(), p.estoqueMinimo() - p.quantidade(), "Reposição até o mínimo"))
                : Optional.empty();
    }
}
