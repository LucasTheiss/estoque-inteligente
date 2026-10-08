package br.com.estoque.service;

import br.com.estoque.domain.Movimentacao;
import br.com.estoque.domain.Produto;
import br.com.estoque.repository.MovimentacaoRepository;
import br.com.estoque.repository.ProdutoRepository;

public class SaidaEstoque extends OperacaoEstoque {
    public SaidaEstoque(ProdutoRepository produtos, MovimentacaoRepository movimentacoes) {
        super(produtos, movimentacoes);
    }

    @Override protected int calcularSaldo(Produto atual, int quantidade) {
        if (atual.quantidade() < quantidade) throw new IllegalStateException("Estoque insuficiente para " + atual.sku());
        return atual.quantidade() - quantidade;
    }

    @Override protected Movimentacao.Tipo tipo() { return Movimentacao.Tipo.SAIDA; }
}
