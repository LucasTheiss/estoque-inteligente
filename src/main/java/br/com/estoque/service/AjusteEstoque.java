package br.com.estoque.service;

import br.com.estoque.domain.Movimentacao;
import br.com.estoque.domain.Produto;
import br.com.estoque.repository.MovimentacaoRepository;
import br.com.estoque.repository.ProdutoRepository;
import java.time.Instant;

public class AjusteEstoque extends OperacaoEstoque {
    private final ProdutoRepository produtos;
    private final MovimentacaoRepository movimentacoes;

    public AjusteEstoque(ProdutoRepository produtos, MovimentacaoRepository movimentacoes) {
        this.produtos = produtos; this.movimentacoes = movimentacoes;
    }

    @Override protected Movimentacao aplicar(String sku, int novaQuantidade) {
        Produto atual = produtos.buscarPorSku(sku).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + sku));
        int diferenca = Math.abs(novaQuantidade - atual.quantidade());
        if (diferenca == 0) throw new IllegalArgumentException("Ajuste não altera a quantidade");
        produtos.salvar(new Produto(sku, atual.nome(), novaQuantidade, atual.estoqueMinimo(), atual.custoUnitario()));
        return new Movimentacao(sku, diferenca, Movimentacao.Tipo.AJUSTE, Instant.now());
    }

    @Override protected void registrar(Movimentacao movimentacao) { movimentacoes.registrar(movimentacao); }
}
