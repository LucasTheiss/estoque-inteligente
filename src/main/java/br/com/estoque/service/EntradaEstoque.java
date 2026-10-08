package br.com.estoque.service;

import br.com.estoque.domain.Movimentacao;
import br.com.estoque.domain.Produto;
import br.com.estoque.repository.MovimentacaoRepository;
import br.com.estoque.repository.ProdutoRepository;

public class EntradaEstoque extends OperacaoEstoque {
    public EntradaEstoque(ProdutoRepository produtos, MovimentacaoRepository movimentacoes) {
        super(produtos, movimentacoes);
    }

    @Override protected int calcularSaldo(Produto atual, int quantidade) {
        return Math.addExact(atual.quantidade(), quantidade);
    }

    @Override protected Movimentacao.Tipo tipo() { return Movimentacao.Tipo.ENTRADA; }
}
