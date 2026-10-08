package br.com.estoque.service;

import br.com.estoque.domain.Movimentacao;
import br.com.estoque.domain.Produto;
import br.com.estoque.repository.MovimentacaoRepository;
import br.com.estoque.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OperacaoEstoqueTest {
    private final ProdutoRepository produtos = mock(ProdutoRepository.class);
    private final MovimentacaoRepository movimentacoes = mock(MovimentacaoRepository.class);
    private final EntradaEstoque entrada = new EntradaEstoque(produtos, movimentacoes);
    private final SaidaEstoque saida = new SaidaEstoque(produtos, movimentacoes);
    private final AjusteEstoque ajuste = new AjusteEstoque(produtos, movimentacoes);

    private void saldo(int quantidade) {
        when(produtos.buscarPorSku("SKU-1")).thenReturn(Optional.of(
                new Produto("SKU-1", "Café", quantidade, 5, BigDecimal.TEN)));
    }

    private void verificar(Movimentacao movimento, int saldo, int quantidade, Movimentacao.Tipo tipo) {
        var gravado = ArgumentCaptor.forClass(Produto.class);
        var ordem = inOrder(produtos, movimentacoes);
        ordem.verify(produtos).buscarPorSku("SKU-1");
        ordem.verify(produtos).salvar(gravado.capture());
        ordem.verify(movimentacoes).registrar(movimento);
        ordem.verifyNoMoreInteractions();
        assertEquals(new Produto("SKU-1", "Café", saldo, 5, BigDecimal.TEN), gravado.getValue());
        assertEquals("SKU-1", movimento.sku());
        assertEquals(quantidade, movimento.quantidade());
        assertEquals(tipo, movimento.tipo());
        assertNotNull(movimento.realizadaEm());
    }

    @Test void entradaSomaERegistra() {
        saldo(3);
        verificar(entrada.executar("SKU-1", 2), 5, 2, Movimentacao.Tipo.ENTRADA);
    }

    @Test void saidaPodeEsgotarEstoque() {
        saldo(3);
        verificar(saida.executar("SKU-1", 3), 0, 3, Movimentacao.Tipo.SAIDA);
    }

    @Test void ajustePodeZerarEstoque() {
        saldo(3);
        verificar(ajuste.executar("SKU-1", 0), 0, 3, Movimentacao.Tipo.AJUSTE);
    }

    @Test void ajusteDefineSaldoFinalERegistraDiferenca() {
        saldo(3);
        verificar(ajuste.executar("SKU-1", 8), 8, 5, Movimentacao.Tipo.AJUSTE);
    }

    @Test void entradasInvalidasNaoAcessamRepositorios() {
        for (OperacaoEstoque operacao : new OperacaoEstoque[] { entrada, saida, ajuste }) {
            assertThrows(IllegalArgumentException.class, () -> operacao.executar(null, 1));
            assertThrows(IllegalArgumentException.class, () -> operacao.executar(" ", 1));
            assertThrows(IllegalArgumentException.class, () -> operacao.executar("SKU-1", -1));
        }
        assertThrows(IllegalArgumentException.class, () -> entrada.executar("SKU-1", 0));
        assertThrows(IllegalArgumentException.class, () -> saida.executar("SKU-1", 0));
        verifyNoInteractions(produtos, movimentacoes);
    }

    @Test void produtoInexistenteNaoGeraEscritas() {
        when(produtos.buscarPorSku("SKU-1")).thenReturn(Optional.empty());
        for (OperacaoEstoque operacao : new OperacaoEstoque[] { entrada, saida, ajuste }) {
            assertThrows(IllegalArgumentException.class, () -> operacao.executar("SKU-1", 1));
        }
        verify(produtos, never()).salvar(any());
        verifyNoInteractions(movimentacoes);
    }

    @Test void falhasDeSaldoNaoGeramEscritas() {
        saldo(3);
        assertThrows(IllegalStateException.class, () -> saida.executar("SKU-1", 4));
        assertThrows(IllegalArgumentException.class, () -> ajuste.executar("SKU-1", 3));
        saldo(Integer.MAX_VALUE);
        assertThrows(ArithmeticException.class, () -> entrada.executar("SKU-1", 1));
        verify(produtos, never()).salvar(any());
        verifyNoInteractions(movimentacoes);
    }

    @Test void falhaAoSalvarNaoRegistraMovimentacao() {
        saldo(3);
        var falha = new IllegalStateException("Falha de persistência");
        doThrow(falha).when(produtos).salvar(any());
        assertSame(falha, assertThrows(IllegalStateException.class, () -> entrada.executar("SKU-1", 1)));
        verifyNoInteractions(movimentacoes);
    }
}
