package br.com.estoque.strategy;

import br.com.estoque.domain.OrdemReposicao;
import br.com.estoque.domain.Produto;
import java.util.Optional;

public class ReposicaoLoteFixo implements PoliticaReposicao {
    private final int tamanhoLote;

    public ReposicaoLoteFixo(int tamanhoLote) {
        if (tamanhoLote <= 0) throw new IllegalArgumentException("Lote deve ser positivo");
        this.tamanhoLote = tamanhoLote;
    }

    @Override public Optional<OrdemReposicao> calcular(Produto p) {
        return p.quantidade() < p.estoqueMinimo()
                ? Optional.of(new OrdemReposicao(p.sku(), tamanhoLote, "Reposição por lote fixo"))
                : Optional.empty();
    }
}
