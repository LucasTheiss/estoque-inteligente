package br.com.estoque;

import br.com.estoque.config.SegurancaConfig;
import br.com.estoque.dto.*;
import br.com.estoque.model.*;
import br.com.estoque.security.UsuarioAutenticado;
import br.com.estoque.service.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Renderiza os templates reais; serviços simulados isolam o contrato HTTP da persistência. */
@WebMvcTest(properties = "app.demo=true")
@Import(SegurancaConfig.class)
class TelasTest {
    private void capture(org.springframework.test.web.servlet.MvcResult result) throws Exception {
        String directory = System.getProperty("ui.captureDir");
        if (directory == null) return;
        var request = result.getRequest();
        String name =
                request.getRequestURI()
                        + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
        Path output = Path.of(directory);
        Files.createDirectories(output);
        Files.writeString(
                output.resolve(name.replaceAll("[^a-zA-Z0-9]", "_") + ".html"),
                result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);
    }

    @Autowired MockMvc mvc;
    @MockitoBean Acesso acesso;
    @MockitoBean AutenticacaoService autenticacao;
    @MockitoBean LojaService lojas;
    @MockitoBean ProdutoService produtos;
    @MockitoBean ModeloProdutoService modelos;
    @MockitoBean PosicaoService posicoes;
    @MockitoBean VinculoService vinculos;
    @MockitoBean EstoqueService estoque;
    @MockitoBean HistoricoService historico;
    @MockitoBean RelatorioService relatorios;
    @MockitoBean UsuarioService usuarios;
    @MockitoBean PermissaoService permissoes;
    @MockitoBean ConfiguracaoIoTService configuracao;
    @MockitoBean IoTService iot;
    @MockitoBean SaudeService saude;
    @MockitoBean AnaliseService analises;

    final Usuario admin = new Usuario(1L, 1L, "admin", "hash", "admin", 1, true);
    final Loja loja =
            new Loja(1L, "teste", "Loja de teste", true, true, true, true, "localhost", 1883, 1, 10);
    final Posicao posicao =
            new Posicao(
                    1L,
                    1L,
                    "A1",
                    "Principal",
                    0,
                    0,
                    new BigDecimal("30"),
                    "un",
                    "esp32-demo",
                    2,
                    true);
    final Produto produto =
            new Produto(
                    1L,
                    1L,
                    1L,
                    "TENIS-38",
                    "789123",
                    "areia",
                    "38",
                    "un",
                    new BigDecimal("125.90"),
                    "{}",
                    true);
    final ProdutoResumo resumo =
            new ProdutoResumo(
                    produto,
                    new ModeloProduto(1L, 1L, "Tênis de teste", "Aurora", "Calçados"),
                    new BigDecimal("6"));
    final Movimentacao movimento =
            new Movimentacao(
                    1L,
                    1L,
                    1L,
                    1L,
                    1L,
                    "entrada",
                    new BigDecimal("6"),
                    BigDecimal.ZERO,
                    new BigDecimal("6"),
                    "Saldo inicial",
                    OffsetDateTime.parse("2026-10-08T12:00:00Z"),
                    "teste",
                    "teste",
                    null);

    @BeforeEach
    void preparar() {
        var tabela = new TabelaConsulta("Resultado de teste", List.of("SKU", "Nome"), List.of(List.of("A", "<script>")));
        when(analises.consultar(anyLong(), anyString())).thenReturn(tabela);
        when(analises.relatorio(anyLong(), anyString(), any(), any())).thenReturn(tabela);
        when(acesso.usuario()).thenReturn(admin);
        when(acesso.loja(any())).thenReturn(1L);
        when(acesso.permissoes(1L)).thenReturn(Acesso.PERMISSOES.keySet());
        when(lojas.contexto(1L)).thenReturn(loja);
        when(lojas.disponiveisLogin()).thenReturn(List.of(loja));
        when(lojas.listar()).thenReturn(List.of(loja));
        when(lojas.editar(1L)).thenReturn(loja);
        when(produtos.pesquisar(eq(1L), anyString(), anyInt())).thenReturn(List.of(resumo));
        when(produtos.buscar(1L, 1L)).thenReturn(resumo);
        when(modelos.listar(1L)).thenReturn(List.of(resumo.modelo()));
        when(modelos.buscar(1L, 1L)).thenReturn(resumo.modelo());
        when(vinculos.buscar(1L, 1L, 1L)).thenReturn(new Ocupacao(1L, 1L, 1L, BigDecimal.ZERO));
        when(posicoes.listar(1L))
                .thenReturn(List.of(new PosicaoResumo(posicao, new BigDecimal("6"))));
        when(posicoes.buscar(1L, 1L)).thenReturn(posicao);
        when(vinculos.listar(1L))
                .thenReturn(
                        List.of(
                                new VinculoResumo(
                                        new Ocupacao(1L, 1L, 1L, new BigDecimal("6")),
                                        "TENIS-38",
                                        "789123",
                                        "Tênis de teste",
                                        "38",
                                        "areia",
                                        "A1",
                                        "Principal",
                                        "un",
                                        "esp32-demo",
                                        2)));
        when(usuarios.listar(1L)).thenReturn(List.of(admin));
        when(usuarios.buscar(1L, 1L)).thenReturn(admin);
        when(permissoes.matriz(1L)).thenReturn(Set.of("vendedor:consultar"));
        when(configuracao.buscar(1L)).thenReturn(loja);
        var historicoResumo =
                new MovimentacaoResumo(
                        movimento, "TENIS-38", "Tênis de teste", "A1", "admin", false);
        when(historico.consultar(eq(1L), anyString(), anyString(), anyString()))
                .thenReturn(List.of(historicoResumo));
        when(historico.estornaveis(1L)).thenReturn(List.of(historicoResumo));
        when(historico.auditoria(1L))
                .thenReturn(
                        List.of(
                                new AuditoriaResumo(
                                        new RegistroAuditoria(
                                                1L,
                                                1L,
                                                1L,
                                                "entrada",
                                                "Saldo inicial",
                                                movimento.createdAt()),
                                        "admin")));
    }

    @Test
    void loginExisteERenderizaSemSessao() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Entre na sua loja")))
                .andExpect(content().string(containsString("name=\"_csrf\"")))
                .andDo(this::capture);
    }

    @ParameterizedTest
    @ValueSource(strings={"/fornecedores", "/relatorios?tipo=valor", "/relatorios?tipo=fluxo"})
    void novasTelasRenderizamComEscape(String url) throws Exception {
        mvc.perform(get(url).with(user(new UsuarioAutenticado(admin))))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("&lt;script&gt;")))
            .andDo(this::capture);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "/",
                "/produtos",
                "/produtos?id=1",
                "/posicoes",
                "/posicoes?id=1",
                "/vinculos",
                "/vinculos?productId=1&positionId=1",
                "/modelos",
                "/modelos?id=1",
                "/movimentar",
                "/baixa-rapida",
                "/estorno",
                "/historico",
                "/mapa",
                "/ocupacao",
                "/usuarios",
                "/usuarios?id=1",
                "/busca-assistida",
                "/iot/configuracao",
                "/global",
                "/global?id=1"
            })
    void telasResolvemTemplatesEModelos(String rota) throws Exception {
        mvc.perform(get(rota).with(user(new UsuarioAutenticado(admin))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Estoque Inteligente")))
                .andExpect(content().string(containsString("data-theme-select")))
                .andDo(this::capture);
    }

    @Test
    void consultaVaziaMantemBuscaESemDetalheIncorreto() throws Exception {
        when(produtos.pesquisar(eq(1L), anyString(), anyInt())).thenReturn(List.of());
        mvc.perform(get("/?q=ausente").with(user(new UsuarioAutenticado(admin))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nenhum produto encontrado")))
                .andExpect(
                        content().string(not(containsString("aria-label=\"Produto selecionado\""))))
                .andDo(this::capture);
    }

    @Test
    void menuDoVendedorNaoExibeAdministracao() throws Exception {
        when(acesso.permissoes(1L)).thenReturn(Set.of("consultar", "saida"));
        mvc.perform(get("/?perfil=vendedor").with(user(new UsuarioAutenticado(admin))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Baixa rápida")))
                .andExpect(content().string(not(containsString("href=\"/usuarios\""))))
                .andExpect(content().string(not(containsString("href=\"/produtos\""))))
                .andDo(this::capture);
    }

    @Test
    void temaETelasDeErroMantemRecuperacao() throws Exception {
        mvc.perform(get("/assets/theme.js")).andExpect(status().isOk());
        mvc.perform(get("/login?error"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Não foi possível entrar")))
                .andExpect(content().string(containsString("data-theme-select")))
                .andDo(this::capture);
    }

    @Test
    void loginPermaneceDisponivelParaSessaoRevogada() throws Exception {
        when(acesso.usuario())
                .thenThrow(
                        new org.springframework.security.access.AccessDeniedException(
                                "Sessão revogada."));
        mvc.perform(get("/login?error").with(user(new UsuarioAutenticado(admin))))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
        mvc.perform(
                        get("/estoque/login")
                                .contextPath("/estoque")
                                .with(user(new UsuarioAutenticado(admin))))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
        verifyNoInteractions(acesso);
    }

    @Test
    void sessaoRevogadaContinuaSemAcessoAProdutos() throws Exception {
        when(acesso.usuario())
                .thenThrow(
                        new org.springframework.security.access.AccessDeniedException(
                                "Sessão revogada."));
        mvc.perform(get("/produtos").with(user(new UsuarioAutenticado(admin))))
                .andExpect(status().isForbidden())
                .andExpect(view().name("erro"));
        verifyNoInteractions(produtos);
    }

    @Test
    void recursosEAutorizacaoMantemContrato() throws Exception {
        mvc.perform(get("/assets/app.css")).andExpect(status().isOk());
        mvc.perform(get("/assets/app.js")).andExpect(status().isOk());
        mvc.perform(get("/produtos")).andExpect(status().is3xxRedirection());
        mvc.perform(post("/produtos").with(user(new UsuarioAutenticado(admin))))
                .andExpect(status().isForbidden());
    }

    @Test
    void erroDeFormularioRenderizaHtmlSemConversorJson() throws Exception {
        doThrow(new IllegalArgumentException("Confira o SKU."))
                .when(produtos)
                .salvar(eq(1L), any());
        mvc.perform(
                        post("/produtos")
                                .with(user(new UsuarioAutenticado(admin)))
                                .with(csrf())
                                .param("name", "Produto")
                                .param("sku", "SKU")
                                .param("unit", "un")
                                .param("price", "1.00"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("erro"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Confira o SKU.")))
                .andDo(
                        result -> {
                            if (System.getProperty("ui.captureDir") != null) {
                                Files.createDirectories(
                                        Path.of(System.getProperty("ui.captureDir")));
                                Files.writeString(
                                        Path.of(System.getProperty("ui.captureDir"), "erro.html"),
                                        result.getResponse()
                                                .getContentAsString(StandardCharsets.UTF_8),
                                        StandardCharsets.UTF_8);
                            }
                        });
    }

    @Test
    void formularioEnviaSaldoDaGradeAoServico() throws Exception {
        mvc.perform(get("/produtos").with(user(new UsuarioAutenticado(admin))))
                .andExpect(content().string(containsString("name=\"initialPositionId\"")))
                .andExpect(content().string(containsString("name=\"initialQuantities\"")));
        mvc.perform(
                        post("/produtos")
                                .with(user(new UsuarioAutenticado(admin)))
                                .with(csrf())
                                .param("name", "Tênis")
                                .param("sku", "GRADE")
                                .param("unit", "un")
                                .param("price", "20")
                                .param("structure", "grade")
                                .param("sizes", "36,37")
                                .param("initialPositionId", "1")
                                .param("initialQuantities", "36=2\n37=0"))
                .andExpect(redirectedUrl("/produtos"));
        verify(produtos)
                .salvar(
                        eq(1L),
                        argThat(
                                f ->
                                        Long.valueOf(1).equals(f.initialPositionId())
                                                && "36=2\n37=0".equals(f.initialQuantities())));
    }

    @Test
    void apiRecusaHtmlSemTentarSerializarMapComoHtml() throws Exception {
        mvc.perform(
                        get("/api/iot/status")
                                .with(user(new UsuarioAutenticado(admin)))
                                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isNotAcceptable());
        verifyNoInteractions(iot);
    }

    @Test
    void erroDaApiRetornaProblemJson() throws Exception {
        when(iot.status(1L)).thenThrow(new IllegalArgumentException("Conexão indisponível."));
        mvc.perform(
                        get("/api/iot/status")
                                .with(user(new UsuarioAutenticado(admin)))
                                .accept(MediaType.ALL))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Conexão indisponível."));
    }
}
