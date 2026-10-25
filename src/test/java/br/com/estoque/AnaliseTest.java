package br.com.estoque;

import br.com.estoque.service.*;
import br.com.estoque.integration.FornecedorGateway;
import br.com.estoque.integration.OfertaGateway;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** SQL real: cálculos, estornos, filtros, isolamento entre lojas e casos vazios. */
@EnabledIfEnvironmentVariable(named="TEST_DATABASE_URL", matches=".+")
class AnaliseTest {
    JdbcTemplate jdbc;
    JdbcTemplate admin;
    String schema;
    Acesso acesso;
    AnaliseService service;
    final LocalDate dia=LocalDate.of(2026,10,20);

    @BeforeEach void preparar() {
        String url=System.getenv("TEST_DATABASE_URL");
        String user=System.getenv().getOrDefault("DATABASE_USER","estoque");
        String password=System.getenv("DATABASE_PASSWORD");
        schema="analise_"+UUID.randomUUID().toString().replace("-","");
        admin=new JdbcTemplate(new DriverManagerDataSource(url,user,password));
        Flyway.configure().dataSource(url,user,password).schemas(schema).load().migrate();
        jdbc=new JdbcTemplate(new DriverManagerDataSource(url+(url.contains("?")?"&":"?")+"currentSchema="+schema,user,password));
        acesso=mock(Acesso.class);
        service=new AnaliseService(jdbc,acesso,mock(FornecedorGateway.class));
        jdbc.update("INSERT INTO tenants(id,slug,name) VALUES(1,'a','A'),(2,'b','B')");
        jdbc.update("INSERT INTO users(id,tenant_id,username,password_hash,role) VALUES(1,1,'admin','hash','admin')");
        jdbc.update("INSERT INTO models(id,tenant_id,name) VALUES(1,1,'Modelo A'),(2,2,'Modelo B')");
        jdbc.update("INSERT INTO products(id,tenant_id,model_id,sku,unit,price) VALUES(1,1,1,'A','un',10),(2,1,1,'B','kg',20),(3,1,1,'C','un',0),(4,2,2,'OUTRA','un',999)");
        jdbc.update("INSERT INTO positions(id,tenant_id,code,sector,capacity,unit) VALUES(1,1,'P1','S',20,'un'),(2,1,'P2','S',10,'un'),(3,1,'P3','S',10,'kg'),(4,2,'P4','S',10,'un')");
        jdbc.update("INSERT INTO occupancy VALUES(1,1,1,8),(1,1,2,2),(1,2,3,0.5),(2,4,4,10)");
        jdbc.update("""
            INSERT INTO movements(tenant_id,product_id,position_id,actor_id,kind,delta,before_qty,after_qty,reason,created_at,request_key,fingerprint)
            VALUES(1,1,1,1,'saida',-2,10,8,'teste','2026-10-21 02:59:00+00','s','s'),
            (1,1,1,1,'estorno',2,8,10,'teste','2026-10-21 02:59:30+00','e','e'),
            (1,1,1,1,'entrada',1,10,11,'fora','2026-10-21 03:00:00+00','f','f')
            """);
    }
    @AfterEach void limpar() { if(admin!=null && schema!=null) admin.execute("DROP SCHEMA IF EXISTS "+schema+" CASCADE"); }
    @Test void calculaValorSemMisturarLojasOuUnidades() {
        var valor=service.relatorio(1,"valor",dia,dia);
        assertEquals(3,valor.linhas().size());
        assertEquals("100.00",valor.linhas().get(0).get(4));
        assertEquals("10.00",valor.linhas().get(1).get(4));
        assertEquals("0.00",valor.linhas().get(2).get(4));
        assertEquals("110.00",valor.linhas().getFirst().get(5));
        assertTrue(service.relatorio(99,"valor",dia,dia).linhas().isEmpty());
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM audit",Integer.class));
        verify(acesso,atLeastOnce()).exigir(1,"relatorios");
        verify(acesso,atLeastOnce()).recurso(1,"reports");
    }
    @Test void periodoIncluiDiaLocalESeparaEstornos() {
        var fluxo=service.relatorio(1,"fluxo",dia,dia);
        assertEquals(2,fluxo.linhas().size());
        assertEquals(Set.of("estorno","saida"),new HashSet<>(fluxo.linhas().stream().map(l->l.get(2)).toList()));
        assertEquals(0,fluxo.linhas().stream().map(l->new BigDecimal(l.get(4))).reduce(BigDecimal.ZERO,BigDecimal::add).signum());
        assertThrows(IllegalArgumentException.class,()->service.relatorio(1,"fluxo",dia.plusDays(1),dia));
    }
    @Test void autorizacaoInterrompeAntesDaConsulta() {
        doThrow(new org.springframework.security.access.AccessDeniedException("negado")).when(acesso).exigir(1,"relatorios");
        assertThrows(org.springframework.security.access.AccessDeniedException.class,()->service.relatorio(1,"valor",dia,dia));
    }
    @Test void comparaOfertasPorLojaGtinUnidadeECalculaPercentual() {
        jdbc.update("UPDATE products SET barcode='789000000001' WHERE id IN (1,4)");
        jdbc.update("UPDATE products SET barcode='789000000002' WHERE id=2");
        jdbc.update("UPDATE products SET barcode='789000000003' WHERE id=3");
        var gateway=mock(OfertaGateway.class);
        var ofertas=new OfertaService(gateway,jdbc,acesso);
        when(gateway.consultar("","")).thenReturn(List.of(
            oferta("789000000001","un","8"), oferta("789000000002","un","10"),
            oferta("789000000003","un","5"), oferta("789000000099","un","7")));
        var tabela=ofertas.comparar(1,"","");
        assertEquals(2,tabela.linhas().size());
        assertEquals("-2.00",tabela.linhas().get(0).get(7));
        assertEquals("-20.00",tabela.linhas().get(0).get(8));
        assertEquals("5.00",tabela.linhas().get(1).get(7));
        assertTrue(tabela.linhas().get(1).get(8).contains("zero"));
        assertTrue(ofertas.comparar(99,"","").linhas().isEmpty());
        jdbc.update("UPDATE products SET active=false WHERE id=1");
        assertEquals(1,ofertas.comparar(1,"","").linhas().size());
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM audit",Integer.class));
        verify(acesso,atLeastOnce()).recurso(1,"reports");
    }
    @Test void ofertasExigemPermissaoAntesDeAcessarOrigem() {
        var gateway=mock(OfertaGateway.class);
        var ofertas=new OfertaService(gateway,jdbc,acesso);
        doThrow(new org.springframework.security.access.AccessDeniedException("negado")).when(acesso).exigir(1,"consultar");
        doThrow(new org.springframework.security.access.AccessDeniedException("negado")).when(acesso).recurso(1,"reports");
        assertThrows(org.springframework.security.access.AccessDeniedException.class,()->ofertas.consultar(1,"",""));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,()->ofertas.comparar(1,"",""));
        verifyNoInteractions(gateway);
    }
    private OfertaGateway.Oferta oferta(String gtin,String unidade,String preco) {
        return new OfertaGateway.Oferta(gtin,"F1",gtin,"Produto","Marca",unidade,new BigDecimal(preco),BigDecimal.TEN,"2026-10-24");
    }
}
