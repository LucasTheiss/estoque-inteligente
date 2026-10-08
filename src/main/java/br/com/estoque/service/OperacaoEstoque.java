package br.com.estoque.service;

import br.com.estoque.domain.Movimentacao;
import br.com.estoque.domain.Produto;
import br.com.estoque.repository.MovimentacaoRepository;
import br.com.estoque.repository.ProdutoRepository;
import java.time.Instant;
import java.util.Objects;

/** Template Method: organiza as etapas comuns das operações de estoque. */
public abstract class OperacaoEstoque {
    private final ProdutoRepository produtos;
    private final MovimentacaoRepository movimentacoes;

    protected OperacaoEstoque(ProdutoRepository produtos, MovimentacaoRepository movimentacoes) {
        this.produtos = Objects.requireNonNull(produtos, "Repositório de produtos obrigatório");
        this.movimentacoes = Objects.requireNonNull(movimentacoes, "Repositório de movimentações obrigatório");
    }

    public final Movimentacao executar(String sku, int quantidade) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU obrigatório");
        validarQuantidade(quantidade);
        Produto atual = produtos.buscarPorSku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + sku));
        Produto atualizado = new Produto(atual.sku(), atual.nome(), calcularSaldo(atual, quantidade),
                atual.estoqueMinimo(), atual.custoUnitario());
        int diferenca = Math.abs(atualizado.quantidade() - atual.quantidade());
        if (diferenca == 0) throw new IllegalArgumentException("Operação não altera a quantidade");
        Movimentacao resultado = new Movimentacao(atual.sku(), diferenca, tipo(), Instant.now());
        produtos.salvar(atualizado);
        movimentacoes.registrar(resultado);
        return resultado;
    }

    protected void validarQuantidade(int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
    }

    protected abstract int calcularSaldo(Produto atual, int quantidade);
    protected abstract Movimentacao.Tipo tipo();
}
