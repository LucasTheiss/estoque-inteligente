package br.com.estoque.service;

import br.com.estoque.domain.Movimentacao;
import br.com.estoque.domain.Produto;
import br.com.estoque.repository.MovimentacaoRepository;
import br.com.estoque.repository.ProdutoRepository;

public class AjusteEstoque extends OperacaoEstoque {
    public AjusteEstoque(ProdutoRepository produtos, MovimentacaoRepository movimentacoes) {
        super(produtos, movimentacoes);
    }

    @Override protected void validarQuantidade(int novaQuantidade) {
        if (novaQuantidade < 0) throw new IllegalArgumentException("Estoque não pode ser negativo");
    }

    @Override protected int calcularSaldo(Produto atual, int novaQuantidade) { return novaQuantidade; }

    @Override protected Movimentacao.Tipo tipo() { return Movimentacao.Tipo.AJUSTE; }
}
