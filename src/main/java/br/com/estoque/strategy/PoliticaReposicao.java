package br.com.estoque.strategy;

import br.com.estoque.domain.OrdemReposicao;
import br.com.estoque.domain.Produto;
import java.util.Optional;

public interface PoliticaReposicao {
    Optional<OrdemReposicao> calcular(Produto produto);
}
