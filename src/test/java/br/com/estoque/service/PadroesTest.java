package br.com.estoque.service;

import br.com.estoque.config.*;
import br.com.estoque.dto.*;
import br.com.estoque.model.*;
import br.com.estoque.repository.*;
import br.com.estoque.strategy.*;
import br.com.estoque.view.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PadroesTest {
    @Test
    void singletonsCompartilhamIdentidadeECatalogoImutavel() {
        assertSame(SistemaConfig.getInstance(), SistemaConfig.getInstance());
        assertEquals("Estoque Inteligente", SistemaConfig.getInstance().get("sistema.nome"));
        var catalogo = CatalogoPermissoes.getInstance();
        assertSame(catalogo, CatalogoPermissoes.getInstance());
        assertSame(catalogo.permissoes(), Acesso.PERMISSOES);
        assertThrows(
                UnsupportedOperationException.class,
                () -> catalogo.permissoes().put("invasao", "Inválida"));
        assertThrows(UnsupportedOperationException.class, () -> catalogo.perfis().add("invasor"));
    }

    @Test
    void estrategiasDeQuantidadeEOperacaoSaoIntercambiaveis() {
        List<Quantidade> politicas =
                List.of(
                        new QuantidadeInteira(),
                        new QuantidadeFracionada(),
                        new QuantidadeMonetaria());
        for (var politica : politicas) assertEquals(new BigDecimal("2"), politica.validar("2"));
        assertThrows(IllegalArgumentException.class, () -> politicas.get(0).validar("0.5"));
        assertEquals(new BigDecimal("0.125"), politicas.get(1).validar("0.125"));
        assertEquals(new BigDecimal("12.50"), politicas.get(2).validar("12.50"));
        assertThrows(IllegalArgumentException.class, () -> politicas.get(2).validar("12.501"));
        Map<String, CalculoEstoque> operacoes =
                Map.of(
                        "entrada",
                        new EntradaEstoque(),
                        "saida",
                        new SaidaEstoque(),
                        "ajuste",
                        new AjusteEstoque());
        assertEquals(
                new BigDecimal("13"),
                operacoes.get("entrada").executar(BigDecimal.TEN, new BigDecimal("3")));
        assertEquals(
                new BigDecimal("7"),
                operacoes.get("saida").executar(BigDecimal.TEN, new BigDecimal("3")));
        assertEquals(
                new BigDecimal("3"),
                operacoes.get("ajuste").executar(BigDecimal.TEN, new BigDecimal("3")));
    }

    @Test
    void templateDeCadastroAutorizaAntesDePersistirEAuditaDepois() {
        var acesso = mock(Acesso.class);
        var auditoria = mock(AuditoriaRepository.class);
        var modelos = mock(ModeloProdutoRepository.class);
        var usuario = new Usuario(7L, 1L, "admin", "hash", "admin", 1, true);
        when(acesso.usuario()).thenReturn(usuario);
        var entidade = new ModeloProduto(null, 1L, "Tênis", "Marca", "Calçados");
        when(modelos.salvar(entidade)).thenReturn(5L);
        var service = new ModeloProdutoService(modelos, acesso, auditoria);
        service.salvar(1L, new ModeloProdutoForm(null, " Tênis ", "Marca", "Calçados"));
        var ordem = inOrder(acesso, modelos, auditoria);
        ordem.verify(acesso).exigir(1L, "cadastros");
        ordem.verify(modelos).salvar(entidade);
        ordem.verify(acesso).usuario();
        ordem.verify(auditoria).registrar(1L, 7L, "modelo.salvar", "5");
        reset(modelos, auditoria);
        doThrow(new AccessDeniedException("Negado")).when(acesso).exigir(1L, "cadastros");
        assertThrows(
                AccessDeniedException.class,
                () -> service.salvar(1L, new ModeloProdutoForm(null, "Outro", "", "")));
        verifyNoInteractions(modelos, auditoria);
    }

    @Test
    void templateDeRelatorioPreservaFormatoEImpedeMisturarLojas() throws Exception {
        var movimento =
                new Movimentacao(
                        1L,
                        1L,
                        1L,
                        1L,
                        7L,
                        "entrada",
                        BigDecimal.ONE,
                        BigDecimal.ZERO,
                        BigDecimal.ONE,
                        "=FORMULA",
                        OffsetDateTime.now(),
                        "chave",
                        "hash",
                        null);
        var registros =
                List.of(new MovimentacaoResumo(movimento, "SKU-1", "Tênis", "A1", "admin", false));
        FormatoRelatorio csv = new RelatorioCsv();
        FormatoRelatorio pdf = new RelatorioPdf();
        var arquivo = csv.gerar(1L, registros);
        assertEquals("historico.csv", arquivo.nome());
        assertTrue(
                new String(arquivo.conteudo(), java.nio.charset.StandardCharsets.UTF_8)
                        .contains("'=FORMULA"));
        var documento = pdf.gerar(1L, registros);
        assertEquals("application/pdf", documento.tipo());
        assertTrue(
                new String(documento.conteudo(), 0, 4, java.nio.charset.StandardCharsets.US_ASCII)
                        .equals("%PDF"));
        for (var formato : List.of(csv, pdf)) {
            assertThrows(IllegalArgumentException.class, () -> formato.gerar(2L, registros));
            assertTrue(formato.gerar(1L, List.of()).conteudo().length > 0);
        }
    }
}
