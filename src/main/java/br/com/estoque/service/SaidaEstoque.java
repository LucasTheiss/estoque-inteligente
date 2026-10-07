package br.com.estoque.service;

import br.com.estoque.domain.Movimentacao;
import br.com.estoque.domain.Produto;
import br.com.estoque.repository.MovimentacaoRepository;
import br.com.estoque.repository.ProdutoRepository;
import java.time.Instant;

public class SaidaEstoque extends OperacaoEstoque {
    private final ProdutoRepository produtos;
    private final MovimentacaoRepository movimentacoes;

    public SaidaEstoque(ProdutoRepository produtos, MovimentacaoRepository movimentacoes) {
        this.produtos = produtos; this.movimentacoes = movimentacoes;
    }

    @Override protected Movimentacao aplicar(String sku, int quantidade) {
        Produto atual = produtos.buscarPorSku(sku).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + sku));
        if (atual.quantidade() < quantidade) throw new IllegalStateException("Estoque insuficiente para " + sku);
        produtos.salvar(new Produto(sku, atual.nome(), atual.quantidade() - quantidade, atual.estoqueMinimo(), atual.custoUnitario()));
        return new Movimentacao(sku, quantidade, Movimentacao.Tipo.SAIDA, Instant.now());
    }

    @Override protected void registrar(Movimentacao movimentacao) { movimentacoes.registrar(movimentacao); }
}
