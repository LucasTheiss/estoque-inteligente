package br.com.estoque;

import br.com.estoque.config.*;
import br.com.estoque.model.Usuario;
import br.com.estoque.repository.UsuarioRepository;
import br.com.estoque.security.UsuarioAutenticado;
import br.com.estoque.dto.MovimentoForm;
import br.com.estoque.model.Movimentacao;
import org.springframework.jdbc.core.JdbcTemplate;
import br.com.estoque.service.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.*;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** PostgreSQL real; esquema exclusivo por execução, sem apagar dados de outras execuções. */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "app.demo=true",
            "app.demo-password=Teste-local-2026!",
            "app.superadmin-password=Teste-global-2026!",
            "app.demo-iot=false"
        })
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "TEST_DATABASE_URL", matches = ".+")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AplicacaoTest {
    static final String SCHEMA = "it_" + UUID.randomUUID().toString().replace("-", "");

    @DynamicPropertySource
    static void banco(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", () -> System.getenv("TEST_DATABASE_URL"));
        r.add(
                "spring.datasource.username",
                () -> System.getenv().getOrDefault("DATABASE_USER", "estoque"));
        r.add("spring.datasource.password", () -> System.getenv("DATABASE_PASSWORD"));
        r.add("spring.flyway.default-schema", () -> SCHEMA);
        r.add("spring.datasource.hikari.connection-init-sql", () -> "SET search_path TO " + SCHEMA);
        r.add("app.demo-iot", () -> System.getenv("TEST_MQTT_URI") != null);
        r.add(
                "app.demo-mqtt-uri",
                () -> System.getenv().getOrDefault("TEST_MQTT_URI", "tcp://localhost:1883"));
    }

    @org.springframework.boot.test.web.server.LocalServerPort int porta;
    BancoTeste banco;
    @Autowired UsuarioRepository usuarios;

    @Autowired
    void bancoTeste(JdbcTemplate jdbc) {
        banco = new BancoTeste(jdbc);
    }

    @Autowired EstoqueService estoque;
    @Autowired Acesso acesso;
    @Autowired DemonstracaoConfig demo;
    @Autowired MockMvc mvc;
    @Autowired IoTService iot;

    Usuario usuario(String loja, String role) {
        return usuarios.autenticar(loja, role).orElseThrow();
    }

    void autenticar(Usuario u) {
        SecurityContextHolder.getContext()
                .setAuthentication(
                        UsernamePasswordAuthenticationToken.authenticated(
                                new UsuarioAutenticado(u),
                                null,
                                new UsuarioAutenticado(u).getAuthorities()));
    }

    @AfterEach
    void sair() {
        SecurityContextHolder.clearContext();
    }

    @AfterAll
    void limpar() {
        banco.executar("DROP SCHEMA " + SCHEMA + " CASCADE");
    }

    long[] saldo(String loja, String unit, String quantidade) {
        var u = usuario(loja, "admin");
        autenticar(u);
        long tenant = u.lojaId();
        long modelo = banco.id("SELECT id FROM models WHERE tenant_id=? LIMIT 1", tenant);
        long pos =
                banco.id(
                        "SELECT id FROM positions WHERE tenant_id=? AND unit=? LIMIT 1",
                        tenant,
                        unit);
        long prod =
                banco.id(
                        "INSERT INTO products(tenant_id,model_id,sku,unit,price) VALUES(?,?,?, ?,1.25) RETURNING id",
                        tenant,
                        modelo,
                        UUID.randomUUID().toString(),
                        unit);
        banco.executar(
                "INSERT INTO occupancy VALUES(?,?,?,?::numeric)", tenant, prod, pos, quantidade);
        return new long[] {tenant, prod, pos};
    }

    Movimentacao mover(long[] p, String tipo, String qtd, String key, Long original) {
        return original == null
                ? estoque.movimentar(
                        p[0],
                        new MovimentoForm(p[1], p[2], tipo, qtd, "Verificação automatizada", key))
                : estoque.estornar(p[0], original, "Verificação automatizada", key);
    }

    @Test
    void demonstracaoTemTresLojasQuatroPerfisEReinicioIdempotente() {
        assertEquals(3, banco.listar("SELECT * FROM tenants").size());
        for (var t : banco.listar("SELECT * FROM tenants")) {
            long loja = ((Number) t.get("id")).longValue();
            String slug = (String) t.get("slug");
            assertEquals(!slug.equals("essencial"), t.get("iot"));
            assertEquals(!slug.equals("essencial"), t.get("enterprise"));
            assertEquals(slug.equals("granel"), t.get("fractional"));
            assertEquals(
                    4,
                    banco.listar("SELECT * FROM users WHERE tenant_id=? AND active", loja).size());
            assertEquals(
                    8,
                    banco.id(
                            "SELECT count(*) id FROM positions WHERE tenant_id=? AND active",
                            loja));
            assertEquals(
                    Acesso.PERMISSOES.size(),
                    banco.id(
                            "SELECT count(*) id FROM role_permissions WHERE tenant_id=? AND role='admin'",
                            loja));
            for (int i = 1; i <= 4; i++) {
                var produto =
                        banco.um(
                                "SELECT p.id,p.unit,o.quantity,s.unit position_unit FROM products p JOIN occupancy o ON o.tenant_id=p.tenant_id AND o.product_id=p.id JOIN positions s ON s.tenant_id=o.tenant_id AND s.id=o.position_id WHERE p.tenant_id=? AND p.sku=?",
                                loja,
                                slug.toUpperCase(Locale.ROOT) + "-" + i);
                assertEquals(slug.equals("granel") ? "kg" : "un", produto.get("unit"));
                assertEquals(produto.get("unit"), produto.get("position_unit"));
                assertEquals(
                        0,
                        BigDecimal.valueOf(6 + (i - 1) * 3)
                                .compareTo((BigDecimal) produto.get("quantity")));
                assertEquals(
                        1,
                        banco.id(
                                "SELECT count(*) id FROM movements WHERE tenant_id=? AND product_id=? AND kind='entrada'",
                                loja,
                                produto.get("id")));
            }
            assertEquals(
                    1,
                    banco.id(
                            "SELECT count(*) id FROM audit WHERE tenant_id=? AND action='demo.criar'",
                            loja));
            autenticar(usuario(slug, "admin"));
            if (slug.equals("essencial")) {
                for (String recurso : List.of("iot", "enterprise", "fractional"))
                    assertThrows(
                            org.springframework.security.access.AccessDeniedException.class,
                            () -> acesso.recurso(loja, recurso));
            } else {
                acesso.recurso(loja, "iot");
                acesso.recurso(loja, "enterprise");
                if (slug.equals("granel")) acesso.recurso(loja, "fractional");
                else
                    assertThrows(
                            org.springframework.security.access.AccessDeniedException.class,
                            () -> acesso.recurso(loja, "fractional"));
            }
        }
        var produtosAntes = banco.listar("SELECT id,sku,price,active FROM products ORDER BY id");
        var saldosAntes =
                banco.listar("SELECT * FROM occupancy ORDER BY tenant_id,product_id,position_id");
        var usuariosAntes =
                banco.listar("SELECT id,password_hash,auth_version,active FROM users ORDER BY id");
        long antes = banco.id("SELECT count(*) id FROM movements");
        demo.run(new org.springframework.boot.DefaultApplicationArguments());
        assertEquals(antes, banco.id("SELECT count(*) id FROM movements"));
        assertEquals(
                produtosAntes,
                banco.listar("SELECT id,sku,price,active FROM products ORDER BY id"));
        assertEquals(
                saldosAntes,
                banco.listar("SELECT * FROM occupancy ORDER BY tenant_id,product_id,position_id"));
        assertEquals(
                usuariosAntes,
                banco.listar("SELECT id,password_hash,auth_version,active FROM users ORDER BY id"));
        autenticar(usuario("global", "superadmin"));
        acesso.global();
        assertTrue(acesso.permissoes(banco.id("SELECT id FROM tenants LIMIT 1")).contains("rbac"));
    }

    @Test
    void idempotenciaEstornoEAuditoriaSaoAtomicos() {
        long[] p = saldo("calcados", "un", "3");
        String key = UUID.randomUUID().toString();
        var saida = mover(p, "saida", "2", key, null);
        assertEquals(saida.id(), mover(p, "saida", "2", key, null).id());
        assertThrows(IllegalArgumentException.class, () -> mover(p, "saida", "1", key, null));
        assertThrows(
                IllegalArgumentException.class, () -> mover(p, "saida", "2", "negativo", null));
        assertEquals(1, banco.listar("SELECT * FROM movements WHERE product_id=?", p[1]).size());
        mover(p, "estorno", "2", UUID.randomUUID().toString(), saida.id());
        assertThrows(
                IllegalArgumentException.class,
                () -> mover(p, "estorno", "2", UUID.randomUUID().toString(), saida.id()));
        assertEquals(
                0,
                new BigDecimal("3")
                        .compareTo(
                                (BigDecimal)
                                        banco.um(
                                                        "SELECT quantity FROM occupancy WHERE product_id=?",
                                                        p[1])
                                                .get("quantity")));
        assertFalse(
                banco.listar("SELECT * FROM audit WHERE action='estorno' AND tenant_id=?", p[0])
                        .isEmpty());
    }

    @Test
    void concorrenciaNuncaVendeAMesmaUnidadeDuasVezes() throws Exception {
        long[] p = saldo("calcados", "un", "1");
        var u = usuario("calcados", "admin");
        var inicio = new CountDownLatch(1);
        try (var pool = Executors.newFixedThreadPool(2)) {
            List<Future<Boolean>> jobs = new ArrayList<>();
            for (int i = 0; i < 2; i++)
                jobs.add(
                        pool.submit(
                                () -> {
                                    autenticar(u);
                                    inicio.await();
                                    try {
                                        mover(p, "saida", "1", UUID.randomUUID().toString(), null);
                                        return true;
                                    } catch (IllegalArgumentException e) {
                                        return false;
                                    } finally {
                                        SecurityContextHolder.clearContext();
                                    }
                                }));
            inicio.countDown();
            int sucesso = 0;
            for (var job : jobs) if (job.get(10, TimeUnit.SECONDS)) sucesso++;
            assertEquals(1, sucesso);
        }
        assertEquals(
                0,
                ((BigDecimal)
                                banco.um("SELECT quantity FROM occupancy WHERE product_id=?", p[1])
                                        .get("quantity"))
                        .signum());
    }

    @Test
    void fracionadosMantemPrecisaoEUsuariosNaoEscapamDaLoja() {
        long[] p = saldo("granel", "kg", "1.125");
        assertEquals(
                new BigDecimal("1.000"),
                mover(p, "saida", "0.125", UUID.randomUUID().toString(), null).afterQty());
        autenticar(usuario("calcados", "admin"));
        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> mover(p, "saida", "1", "outra-loja", null));
        long[] outro = saldo("calcados", "un", "1");
        autenticar(usuario("calcados", "vendedor"));
        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> mover(outro, "entrada", "1", "sem-permissao", null));
        assertThrows(
                org.springframework.security.access.AccessDeniedException.class, acesso::global);
    }

    @Test
    void loginExigeLojaSenhaECsrf() throws Exception {
        mvc.perform(
                        post("/login")
                                .param("loja", "calcados")
                                .param("username", "admin")
                                .param("password", "Teste-local-2026!"))
                .andExpect(status().isForbidden());
        mvc.perform(
                        post("/login")
                                .with(csrf())
                                .param("loja", "calcados")
                                .param("username", "admin")
                                .param("password", "Teste-local-2026!"))
                .andExpect(redirectedUrl("/"));
        mvc.perform(
                        post("/login")
                                .with(csrf())
                                .param("loja", "global")
                                .param("username", "admin")
                                .param("password", "Teste-local-2026!"))
                .andExpect(redirectedUrl("/login?error"));
        mvc.perform(
                        post("/login")
                                .with(csrf())
                                .param("loja", "global")
                                .param("username", "superadmin")
                                .param("password", "Teste-global-2026!"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "TEST_MQTT_URI", matches = ".+")
    void mqttRealTransportaPesoEConfirmaComandoSimulado() throws Exception {
        var u = usuario("granel", "admin");
        autenticar(u);
        var broker = java.net.URI.create(System.getenv("TEST_MQTT_URI"));
        banco.executar(
                "UPDATE tenants SET mqtt_host=?,mqtt_port=? WHERE id=?",
                broker.getHost(),
                broker.getPort() < 0 ? 1883 : broker.getPort(),
                u.lojaId());
        long pos = banco.id("SELECT id FROM positions WHERE tenant_id=? LIMIT 1", u.lojaId());
        long prod = banco.id("SELECT id FROM products WHERE tenant_id=? LIMIT 1", u.lojaId());
        assertEquals(true, iot.status(u.lojaId()).get("connected"));
        Map<String, Object> peso = null;
        for (int n = 0; n < 40 && peso == null; n++) {
            try {
                peso = iot.peso(u.lojaId(), prod, "balanca-demo");
            } catch (IllegalArgumentException e) {
                Thread.sleep(250);
            }
        }
        assertNotNull(peso);
        assertEquals(new BigDecimal("1.425"), peso.get("quantity"));
        assertEquals(true, peso.get("simulated"));
        iot.sinalizar(u.lojaId(), pos);
        Map<String, Object> ack = Map.of();
        for (int n = 0; n < 40; n++) {
            ack = iot.confirmacao(u.lojaId(), pos);
            if (Boolean.TRUE.equals(ack.get("confirmed"))) break;
            Thread.sleep(250);
        }
        assertEquals(true, ack.get("confirmed"));
        assertEquals(true, ack.get("simulated"));
        iot.sinalizar(u.lojaId(), pos);
        assertEquals(
                false,
                iot.confirmacao(u.lojaId(), pos).get("confirmed"),
                "ACK antigo não confirma um novo comando");
    }

    @Test
    void httpRealPermiteLoginCadastroMovimentacaoELogout() throws Exception {
        var cookies = new java.net.CookieManager(null, java.net.CookiePolicy.ACCEPT_ALL);
        try (var client =
                java.net.http.HttpClient.newBuilder()
                        .cookieHandler(cookies)
                        .version(java.net.http.HttpClient.Version.HTTP_1_1)
                        .connectTimeout(java.time.Duration.ofSeconds(5))
                        .build()) {
            var login = httpGet(client, "/login");
            assertEquals(200, login.statusCode());
            assertTrue(login.body().contains("Entre na sua loja"));
            var entrada =
                    httpPost(
                            client,
                            "/login",
                            Map.of(
                                    "loja",
                                    "calcados",
                                    "username",
                                    "admin",
                                    "password",
                                    "Teste-local-2026!",
                                    "_csrf",
                                    token(login.body())));
            assertEquals(302, entrada.statusCode());
            assertEquals(
                    "/",
                    java.net
                            .URI
                            .create(entrada.headers().firstValue("location").orElseThrow())
                            .getPath());

            String sku = "HTTP-" + UUID.randomUUID();
            var cadastro = httpGet(client, "/produtos");
            assertEquals(200, cadastro.statusCode());
            assertEquals(
                    302,
                    httpPost(
                                    client,
                                    "/produtos",
                                    Map.of(
                                            "name",
                                            "Produto HTTP",
                                            "sku",
                                            sku,
                                            "unit",
                                            "un",
                                            "price",
                                            "19.90",
                                            "_csrf",
                                            token(cadastro.body())))
                            .statusCode());
            long loja = usuario("calcados", "admin").lojaId();
            long produto =
                    banco.id("SELECT id FROM products WHERE tenant_id=? AND sku=?", loja, sku);
            long posicao =
                    banco.id(
                            "SELECT id FROM positions WHERE tenant_id=? AND unit='un' LIMIT 1",
                            loja);
            var vinculos = httpGet(client, "/vinculos");
            assertEquals(200, vinculos.statusCode());
            assertEquals(
                    302,
                    httpPost(
                                    client,
                                    "/vinculos",
                                    Map.of(
                                            "productId",
                                            Long.toString(produto),
                                            "positionId",
                                            Long.toString(posicao),
                                            "_csrf",
                                            token(vinculos.body())))
                            .statusCode());

            var movimento = httpGet(client, "/movimentar");
            assertEquals(200, movimento.statusCode());
            assertEquals(
                    302,
                    httpPost(
                                    client,
                                    "/movimentar",
                                    Map.of(
                                            "productId",
                                            Long.toString(produto),
                                            "positionId",
                                            Long.toString(posicao),
                                            "kind",
                                            "entrada",
                                            "quantity",
                                            "2",
                                            "reason",
                                            "Recebimento HTTP",
                                            "requestKey",
                                            UUID.randomUUID().toString(),
                                            "_csrf",
                                            token(movimento.body())))
                            .statusCode());
            assertEquals(
                    0,
                    new BigDecimal("2")
                            .compareTo(
                                    (BigDecimal)
                                            banco.um(
                                                            "SELECT quantity FROM occupancy WHERE tenant_id=? AND product_id=? AND position_id=?",
                                                            loja,
                                                            produto,
                                                            posicao)
                                                    .get("quantity")));
            assertTrue(httpGet(client, "/?q=" + sku).body().contains(sku));
            var relatorio = httpGet(client, "/historico/exportar/csv");
            assertEquals(200, relatorio.statusCode());
            assertTrue(
                    relatorio
                            .headers()
                            .firstValue("content-type")
                            .orElse("")
                            .startsWith("text/csv"));
            assertTrue(relatorio.body().contains(sku));

            var pagina = httpGet(client, "/");
            assertEquals(
                    302,
                    httpPost(client, "/logout", Map.of("_csrf", token(pagina.body())))
                            .statusCode());
            assertEquals(302, httpGet(client, "/").statusCode());
        }
    }

    @Autowired ProdutoService produtos;

    @Test
    void buscaIgnoraAcentosPontuacaoFinalEOrdemSemPerderIsolamento() {
        var u = usuario("calcados", "admin");
        autenticar(u);
        String sku = "BUSCA-" + UUID.randomUUID() + "-38";
        long modelo =
                banco.id(
                        "INSERT INTO models(tenant_id,name,brand,category) VALUES(?,'Tênis Café Açúcar','Única','Calçados') RETURNING id",
                        u.lojaId());
        long produto =
                banco.id(
                        "INSERT INTO products(tenant_id,model_id,sku,color,size,unit,price) VALUES(?,?,?,'Azul','38','un',21.50) RETURNING id",
                        u.lojaId(),
                        modelo,
                        sku);
        long outra = usuario("granel", "admin").lojaId();
        long modeloOutro =
                banco.id(
                        "INSERT INTO models(tenant_id,name) VALUES(?,'Tênis Café Açúcar') RETURNING id",
                        outra);
        banco.executar(
                "INSERT INTO products(tenant_id,model_id,sku,unit,price) VALUES(?,?,?,'un',21.50)",
                outra,
                modeloOutro,
                sku);
        for (String busca :
                List.of(
                        "tenis cafe acucar",
                        "TÊNIS CAFÉ AÇÚCAR.",
                        "cafe, tenis!",
                        "38 azul cafe",
                        "tênis cafe…",
                        "cafe 21.50",
                        sku + ".",
                        "cafe calcados unica")) {
            var encontrados = produtos.pesquisar(u.lojaId(), busca, 0);
            assertEquals(1, encontrados.size(), busca);
            assertEquals(produto, encontrados.getFirst().produto().id());
            assertEquals(u.lojaId(), encontrados.getFirst().produto().tenantId());
        }
        assertTrue(produtos.pesquisar(u.lojaId(), "cafe vermelho", 0).isEmpty());
        assertTrue(produtos.pesquisar(u.lojaId(), "%", 0).isEmpty());
        assertTrue(produtos.pesquisar(u.lojaId(), "_", 0).isEmpty());
    }

    @Test
    void baixaAceitaUnidadeInteiraEPesoFracionadoMasRejeitaFracaoDeUnidade() throws Exception {
        for (String loja : List.of("calcados", "granel")) {
            boolean fracionado = loja.equals("granel");
            long[] p = saldo(loja, fracionado ? "kg" : "un", "3");
            var principal = new UsuarioAutenticado(usuario(loja, "admin"));
            mvc.perform(
                            get("/baixa-rapida")
                                    .with(
                                            org.springframework.security.test.web.servlet.request
                                                    .SecurityMockMvcRequestPostProcessors.user(
                                                    principal)))
                    .andExpect(status().isOk());
            mvc.perform(
                            post("/movimentar")
                                    .with(
                                            org.springframework.security.test.web.servlet.request
                                                    .SecurityMockMvcRequestPostProcessors.user(
                                                    principal))
                                    .with(csrf())
                                    .param("productId", "" + p[1])
                                    .param("positionId", "" + p[2])
                                    .param("kind", "saida")
                                    .param("quantity", fracionado ? "0.125" : "1")
                                    .param("reason", "Baixa rápida")
                                    .param("requestKey", UUID.randomUUID().toString()))
                    .andExpect(status().is3xxRedirection());
            var esperado = new BigDecimal(fracionado ? "2.875" : "2");
            assertEquals(
                    0,
                    esperado.compareTo(
                            (BigDecimal)
                                    banco.um(
                                                    "SELECT quantity FROM occupancy WHERE product_id=? AND position_id=?",
                                                    p[1],
                                                    p[2])
                                            .get("quantity")));
            if (!fracionado) {
                mvc.perform(
                                post("/movimentar")
                                        .with(
                                                org.springframework.security.test.web.servlet
                                                        .request
                                                        .SecurityMockMvcRequestPostProcessors.user(
                                                        principal))
                                        .with(csrf())
                                        .param("productId", "" + p[1])
                                        .param("positionId", "" + p[2])
                                        .param("kind", "saida")
                                        .param("quantity", "1.001")
                                        .param("reason", "Quantidade inválida")
                                        .param("requestKey", UUID.randomUUID().toString()))
                        .andExpect(status().isBadRequest());
                assertEquals(
                        0,
                        esperado.compareTo(
                                (BigDecimal)
                                        banco.um(
                                                        "SELECT quantity FROM occupancy WHERE product_id=? AND position_id=?",
                                                        p[1],
                                                        p[2])
                                                .get("quantity")));
            }
        }
    }

    @Test
    void gradeComSaldoInicialPersisteHistoricoERollbackPreservaAtomicidade() {
        var u = usuario("calcados", "admin");
        autenticar(u);
        long loja = u.lojaId();
        long destino =
                banco.id("SELECT id FROM positions WHERE tenant_id=? AND unit='un' LIMIT 1", loja);
        String sku = "GRADE-" + UUID.randomUUID();
        var form =
                new br.com.estoque.dto.ProdutoForm(
                        null,
                        "Grade integrada",
                        "",
                        "",
                        sku,
                        "",
                        "",
                        "",
                        "un",
                        "20.00",
                        "{}",
                        "grade",
                        "36,37",
                        destino,
                        "36=2\n37=0");
        produtos.salvar(loja, form);
        var resultados =
                banco.listar(
                        "SELECT p.size,o.quantity FROM products p JOIN occupancy o ON o.product_id=p.id AND o.tenant_id=p.tenant_id WHERE p.tenant_id=? AND p.sku IN (?,?) ORDER BY p.size",
                        loja,
                        sku + "-36",
                        sku + "-37");
        assertEquals(2, resultados.size());
        assertEquals(
                0, new BigDecimal("2").compareTo((BigDecimal) resultados.get(0).get("quantity")));
        assertEquals(0, ((BigDecimal) resultados.get(1).get("quantity")).signum());
        assertEquals(
                1,
                banco.id(
                        "SELECT count(*) id FROM movements m JOIN products p ON p.id=m.product_id AND p.tenant_id=m.tenant_id WHERE p.tenant_id=? AND p.sku IN (?,?)",
                        loja,
                        sku + "-36",
                        sku + "-37"));
        String falha = "ROLLBACK-" + UUID.randomUUID();
        long outraLoja = banco.id("SELECT id FROM positions WHERE tenant_id<>? LIMIT 1", loja);
        var invalido =
                new br.com.estoque.dto.ProdutoForm(
                        null,
                        "Grade inválida",
                        "",
                        "",
                        falha,
                        "",
                        "",
                        "",
                        "un",
                        "20.00",
                        "{}",
                        "grade",
                        "36,37",
                        outraLoja,
                        "36=2\n37=0");
        assertThrows(IllegalArgumentException.class, () -> produtos.salvar(loja, invalido));
        assertEquals(
                0,
                banco.id(
                        "SELECT count(*) id FROM products WHERE tenant_id=? AND sku IN (?,?)",
                        loja,
                        falha + "-36",
                        falha + "-37"));
    }

    private java.net.http.HttpResponse<String> httpGet(java.net.http.HttpClient client, String rota)
            throws Exception {
        return client.send(
                java.net.http.HttpRequest.newBuilder(
                                java.net.URI.create("http://127.0.0.1:" + porta + rota))
                        .timeout(java.time.Duration.ofSeconds(15))
                        .GET()
                        .build(),
                java.net.http.HttpResponse.BodyHandlers.ofString());
    }

    private java.net.http.HttpResponse<String> httpPost(
            java.net.http.HttpClient client, String rota, Map<String, String> dados)
            throws Exception {
        String corpo =
                dados.entrySet().stream()
                        .map(
                                e ->
                                        java.net.URLEncoder.encode(
                                                        e.getKey(),
                                                        java.nio.charset.StandardCharsets.UTF_8)
                                                + "="
                                                + java.net.URLEncoder.encode(
                                                        e.getValue(),
                                                        java.nio.charset.StandardCharsets.UTF_8))
                        .collect(java.util.stream.Collectors.joining("&"));
        return client.send(
                java.net.http.HttpRequest.newBuilder(
                                java.net.URI.create("http://127.0.0.1:" + porta + rota))
                        .timeout(java.time.Duration.ofSeconds(15))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(corpo))
                        .build(),
                java.net.http.HttpResponse.BodyHandlers.ofString());
    }

    private static String token(String html) {
        var campo =
                java.util.regex.Pattern.compile(
                                "<input(?=[^>]*name=\"_csrf\")(?=[^>]*value=\"([^\"]+)\")[^>]*>")
                        .matcher(html);
        assertTrue(campo.find(), "O formulário deve publicar um token CSRF.");
        return campo.group(1);
    }
}
