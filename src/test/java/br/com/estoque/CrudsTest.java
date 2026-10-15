package br.com.estoque;

import br.com.estoque.dto.*;
import br.com.estoque.model.*;
import br.com.estoque.repository.*;
import br.com.estoque.security.UsuarioAutenticado;
import br.com.estoque.service.*;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.*;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

/** CRUDs com banco real e esquema isolado; não altera as lojas usadas na apresentação. */
@SpringBootTest(properties={"app.demo=true","app.demo-password=Teste-local-2026!","app.superadmin-password=Teste-global-2026!","app.demo-iot=false"})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named="TEST_DATABASE_URL",matches=".+")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CrudsTest {
    static final String SCHEMA="crud_"+UUID.randomUUID().toString().replace("-","");
    @DynamicPropertySource static void configurar(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",()->System.getenv("TEST_DATABASE_URL"));
        r.add("spring.datasource.username",()->System.getenv().getOrDefault("DATABASE_USER","estoque"));
        r.add("spring.datasource.password",()->System.getenv("DATABASE_PASSWORD"));
        r.add("spring.flyway.default-schema",()->SCHEMA);
        r.add("spring.datasource.hikari.connection-init-sql",()->"SET search_path TO "+SCHEMA);
    }
    @Autowired JdbcTemplate jdbc;
    @Autowired MockMvc mvc;
    @Autowired UsuarioRepository usuarios;
    @Autowired ModeloProdutoService modelos;
    @Autowired LojaService lojas;
    @Autowired VinculoService vinculos;
    @Autowired Acesso acesso;
    @Autowired DemonstracaoService demo;
    @Autowired AutenticacaoService autenticacao;
    @Autowired PosicaoService posicoes;

    Usuario usuario(String loja,String perfil) { return usuarios.autenticar(loja,perfil).orElseThrow(); }
    void autenticar(Usuario u) {
        var principal=new UsuarioAutenticado(u);
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(principal,null,principal.getAuthorities()));
    }
    @AfterEach void sair() { SecurityContextHolder.clearContext(); }
    @AfterAll void limpar() { jdbc.execute("DROP SCHEMA "+SCHEMA+" CASCADE"); }
    long id(String sql,Object... args) { return jdbc.queryForObject(sql,Long.class,args); }
    long produto(long loja) {
        long modelo=id("SELECT id FROM models WHERE tenant_id=? LIMIT 1",loja);
        return id("INSERT INTO products(tenant_id,model_id,sku,unit,price) VALUES(?,?,?,'un',1) RETURNING id",loja,modelo,"CRUD-"+UUID.randomUUID());
    }
    long posicao(long loja,String unidade) {
        return id("INSERT INTO positions(tenant_id,code,sector,capacity,unit) VALUES(?,?,'Teste',100,?) RETURNING id",loja,"CRUD-"+UUID.randomUUID(),unidade);
    }

    @Test void modelosCriamEditamConsultamEExcluemPelaTela() throws Exception {
        var admin=usuario("calcados","admin");var principal=new UsuarioAutenticado(admin);
        String nome="Modelo-"+UUID.randomUUID();
        mvc.perform(post("/modelos").with(user(principal)).with(csrf()).param("name",nome).param("brand","Marca").param("category","Categoria"))
                .andExpect(redirectedUrl("/modelos"));
        long modelo=id("SELECT id FROM models WHERE tenant_id=? AND name=?",admin.lojaId(),nome);
        mvc.perform(get("/modelos?id="+modelo).with(user(principal))).andExpect(status().isOk()).andExpect(content().string(containsString(nome)));
        mvc.perform(post("/modelos").with(user(principal)).with(csrf()).param("id",""+modelo).param("name",nome+" editado").param("brand","Nova marca").param("category","Nova categoria"))
                .andExpect(redirectedUrl("/modelos"));
        assertEquals("Nova marca",jdbc.queryForObject("SELECT brand FROM models WHERE id=?",String.class,modelo));
        mvc.perform(post("/excluir/modelo/"+modelo).with(user(principal)).with(csrf())).andExpect(redirectedUrl("/modelos"));
        assertEquals(0,id("SELECT count(*) FROM models WHERE id=?",modelo));
        assertEquals(3,id("SELECT count(*) FROM audit WHERE tenant_id=? AND action LIKE 'modelo.%' AND detail->>'descricao'=?",admin.lojaId(),""+modelo));
    }

    @ParameterizedTest @ValueSource(strings={"admin","gerente","estoquista","vendedor"})
    void novosCadastrosRespeitamPermissoesReais(String perfil) throws Exception {
        var u=usuario("calcados",perfil);var principal=new UsuarioAutenticado(u);
        boolean permitido=!perfil.equals("vendedor");
        mvc.perform(get("/modelos").with(user(principal))).andExpect(status().is(permitido?200:403));
        mvc.perform(get("/vinculos").with(user(principal))).andExpect(status().is(permitido?200:403));
        mvc.perform(post("/modelos").with(user(principal)).with(csrf()).param("name","Perfil-"+UUID.randomUUID()))
                .andExpect(status().is(permitido?302:403));
        long p=produto(u.lojaId()),s=posicao(u.lojaId(),"un"),destino=posicao(u.lojaId(),"un");
        jdbc.update("INSERT INTO occupancy(tenant_id,product_id,position_id) VALUES(?,?,?)",u.lojaId(),p,s);
        mvc.perform(post("/vinculos").with(user(principal)).with(csrf()).param("action","update")
                .param("productId",""+p).param("positionId",""+destino).param("originalProductId",""+p).param("originalPositionId",""+s))
                .andExpect(status().is(permitido?302:403));
        mvc.perform(get("/global").with(user(principal))).andExpect(status().isForbidden());
        mvc.perform(post("/global/lojas/"+u.lojaId()+"/excluir").with(user(principal)).with(csrf())).andExpect(status().isForbidden());
    }

    @Test void revogarCadastrosBloqueiaServidorESemCsrfNaoMuta() throws Exception {
        var u=usuario("calcados","gerente");var principal=new UsuarioAutenticado(u);
        long modelo=id("SELECT id FROM models WHERE tenant_id=? LIMIT 1",u.lojaId());
        try {
            jdbc.update("DELETE FROM role_permissions WHERE tenant_id=? AND role='gerente' AND permission='cadastros'",u.lojaId());
            mvc.perform(get("/modelos").with(user(principal))).andExpect(status().isForbidden());
            mvc.perform(post("/modelos").with(user(principal)).with(csrf()).param("id",""+modelo).param("name","Proibido")).andExpect(status().isForbidden());
            mvc.perform(post("/excluir/modelo/"+modelo).with(user(principal)).with(csrf())).andExpect(status().isForbidden());
            mvc.perform(post("/vinculos").with(user(principal)).with(csrf()).param("action","update").param("productId","1").param("positionId","1").param("originalProductId","1").param("originalPositionId","1")).andExpect(status().isForbidden());
        } finally {
            jdbc.update("INSERT INTO role_permissions VALUES(?,'gerente','cadastros') ON CONFLICT DO NOTHING",u.lojaId());
        }
        var admin=new UsuarioAutenticado(usuario("calcados","admin"));
        mvc.perform(post("/modelos").with(user(admin)).param("name","Sem CSRF")).andExpect(status().isForbidden());
        mvc.perform(post("/vinculos").with(user(admin)).param("action","update").param("productId","1").param("positionId","1")).andExpect(status().isForbidden());
        mvc.perform(post("/global/lojas/1/excluir").with(user(new UsuarioAutenticado(usuario("global","superadmin"))))).andExpect(status().isForbidden());
    }

    @Test void modelosIsolamLojasEPreservamProdutosInativos() {
        var u=usuario("calcados","admin");autenticar(u);long loja=u.lojaId();
        long outra=usuario("granel","admin").lojaId();
        long modelo=id("SELECT id FROM models WHERE tenant_id=? LIMIT 1",outra);
        assertThrows(IllegalArgumentException.class,()->modelos.buscar(loja,modelo));
        assertThrows(IllegalArgumentException.class,()->modelos.salvar(loja,new ModeloProdutoForm(modelo,"Invasão","","")));
        assertThrows(IllegalArgumentException.class,()->modelos.excluir(loja,modelo));
        assertThrows(AccessDeniedException.class,()->modelos.listar(outra));
        long p=produto(loja);long emUso=id("SELECT model_id FROM products WHERE id=?",p);
        jdbc.update("UPDATE products SET active=false WHERE id=?",p);
        assertThrows(IllegalArgumentException.class,()->modelos.excluir(loja,emUso));
        assertEquals(1,id("SELECT count(*) FROM models WHERE id=?",emUso));
    }

    @Test void vinculosEditamSemMoverSaldoERollbackPreservaOriginal() throws Exception {
        var admin=usuario("calcados","admin");long loja=admin.lojaId();autenticar(admin);
        long p=produto(loja),a=posicao(loja,"un"),b=posicao(loja,"un"),kg=posicao(loja,"kg");
        vinculos.salvar(loja,p,a,false);
        var principal=new UsuarioAutenticado(admin);
        mvc.perform(get("/vinculos?productId="+p+"&positionId="+a).with(user(principal))).andExpect(status().isOk());
        mvc.perform(post("/vinculos").with(user(principal)).with(csrf()).param("action","update").param("originalProductId",""+p)
                .param("originalPositionId",""+a).param("productId",""+p).param("positionId",""+b)).andExpect(redirectedUrl("/vinculos"));
        autenticar(admin);
        assertEquals(0,id("SELECT count(*) FROM occupancy WHERE product_id=? AND position_id=?",p,a));
        assertEquals(0,vinculos.buscar(loja,p,b).quantity().signum());
        vinculos.salvar(loja,p,a,false);
        assertThrows(org.springframework.dao.DataAccessException.class,()->vinculos.editar(loja,p,b,p,a));
        assertEquals(0,vinculos.buscar(loja,p,b).quantity().signum());
        assertThrows(IllegalArgumentException.class,()->vinculos.editar(loja,p,b,p,kg));
        long outra=usuario("granel","admin").lojaId();long fora=posicao(outra,"un");
        assertThrows(IllegalArgumentException.class,()->vinculos.editar(loja,p,b,p,fora));
        jdbc.update("UPDATE occupancy SET quantity=2 WHERE tenant_id=? AND product_id=? AND position_id=?",loja,p,b);
        assertThrows(IllegalArgumentException.class,()->vinculos.editar(loja,p,b,p,a));
        assertEquals(0,new BigDecimal("2").compareTo(vinculos.buscar(loja,p,b).quantity()));
        jdbc.update("UPDATE occupancy SET quantity=0 WHERE tenant_id=? AND product_id=? AND position_id=?",loja,p,b);
        mvc.perform(post("/vinculos").with(user(principal)).with(csrf()).param("action","delete").param("productId",""+p).param("positionId",""+b)).andExpect(redirectedUrl("/vinculos"));
        assertEquals(0,id("SELECT count(*) FROM occupancy WHERE product_id=? AND position_id=?",p,b));
    }

    @Test void lojasCriamEditamEDesativamBloqueandoLoginESessoes() throws Exception {
        var global=usuario("global","superadmin");var principal=new UsuarioAutenticado(global);
        String slug="crud-"+UUID.randomUUID().toString().substring(0,8);
        mvc.perform(post("/global/lojas").with(user(principal)).with(csrf()).param("slug",slug).param("name","Loja CRUD")
                .param("username","admin").param("password","Teste-local-2026!")).andExpect(redirectedUrl("/global"));
        long loja=id("SELECT id FROM tenants WHERE slug=?",slug);var local=usuario(slug,"admin");
        mvc.perform(get("/global?id="+loja).with(user(principal))).andExpect(status().isOk());
        mvc.perform(post("/global/lojas").with(user(principal)).with(csrf()).param("id",""+loja).param("slug",slug)
                .param("name","Loja editada").param("fractional","true")).andExpect(redirectedUrl("/global"));
        assertEquals("Loja editada",jdbc.queryForObject("SELECT name FROM tenants WHERE id=?",String.class,loja));
        assertEquals(Boolean.TRUE,jdbc.queryForObject("SELECT fractional FROM tenants WHERE id=?",Boolean.class,loja));
        mvc.perform(post("/global/lojas/"+loja+"/excluir").with(user(principal)).with(csrf()).sessionAttr("loja",loja))
                .andExpect(redirectedUrl("/global"));
        assertEquals(Boolean.FALSE,jdbc.queryForObject("SELECT active FROM tenants WHERE id=?",Boolean.class,loja));
        assertTrue(usuarios.autenticar(slug,"admin").isEmpty());
        assertTrue(lojas.disponiveisLogin().stream().noneMatch(l->l.id()==loja));
        assertThrows(org.springframework.security.authentication.BadCredentialsException.class,()->autenticacao.autenticar(slug,"admin","Teste-local-2026!"));
        mvc.perform(get("/modelos").with(user(new UsuarioAutenticado(local)))).andExpect(status().isForbidden());
        mvc.perform(get("/modelos").with(user(principal)).sessionAttr("loja",loja)).andExpect(status().isForbidden());
        mvc.perform(get("/global").with(user(principal)).sessionAttr("loja",loja)).andExpect(status().isOk());
        assertEquals(1,id("SELECT count(*) FROM users WHERE tenant_id=?",loja));
        assertEquals(1,id("SELECT count(*) FROM audit WHERE tenant_id=? AND action='loja.excluir'",loja));
    }

    @Test void templateDePosicaoMantemTransacaoEAuditoria() {
        var u=usuario("calcados","admin");autenticar(u);
        String codigo="TM-"+UUID.randomUUID();
        posicoes.salvar(u.lojaId(),new PosicaoForm(null,codigo,"Teste",0,0,"10","un","",null));
        long posicao=id("SELECT id FROM positions WHERE tenant_id=? AND code=?",u.lojaId(),codigo);
        assertEquals(1,id("SELECT count(*) FROM audit WHERE tenant_id=? AND action='posicao.salvar' AND detail->>'descricao'=?",u.lojaId(),""+posicao));
        assertThrows(IllegalArgumentException.class,()->posicoes.salvar(u.lojaId(),new PosicaoForm(posicao,codigo,"Teste",0,0,"0","un","",null)));
        assertEquals(0,new BigDecimal("10").compareTo(jdbc.queryForObject("SELECT capacity FROM positions WHERE id=?",BigDecimal.class,posicao)));
    }
}
