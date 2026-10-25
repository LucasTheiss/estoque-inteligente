package br.com.estoque.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class OfertaGatewayTest {
    HttpServer server;
    String resposta;
    int status;
    OfertaGateway gateway;
    @BeforeEach void preparar() throws Exception {
        status=200;
        resposta="""
            [{"gtin":"789000000001","fornecedor":"F1","sku":"EXT1","descricao":"Produto",
            "marca":"Marca","unidade":"un","preco":12.50,"disponibilidade":2,"atualizacao":"2026-10-24"}]
            """;
        server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/ofertas", e->{
            assertEquals("GET",e.getRequestMethod());
            byte[] body=resposta.getBytes(StandardCharsets.UTF_8);
            e.sendResponseHeaders(status,body.length);e.getResponseBody().write(body);e.close();
        });
        server.start();
        gateway=new OfertaGateway("http://127.0.0.1:"+server.getAddress().getPort()+"/ofertas",new ObjectMapper());
    }
    @AfterEach void parar(){server.stop(0);}
    @Test void filtrosExatosEAtualizacaoDaOrigem(){
        assertEquals(1,gateway.consultar("789000000001","f1").size());
        assertTrue(gateway.consultar("789","F1").isEmpty());
        assertTrue(gateway.consultar("","F2").isEmpty());
        resposta="[]";
        assertTrue(gateway.consultar("","").isEmpty());
    }
    @Test void contratoInvalidoEFalhaNaoViramResultadoVazio(){
        String valida=resposta;
        for(String invalida : new String[]{"null", "[null]", "[{}]", valida.replace("12.50","-1"),
                valida.replace("2026-10-24","2026-02-30"), valida.replace("\"disponibilidade\":2","\"disponibilidade\":2.5"),
                " ".repeat(262145)}) {
            resposta=invalida;
            assertThrows(IllegalArgumentException.class,()->gateway.consultar("",""));
        }
        resposta=valida; status=503;
        assertThrows(IllegalArgumentException.class,()->gateway.consultar("",""));
    }
    @Test void filtrosLongosSaoRejeitados(){
        assertThrows(IllegalArgumentException.class,()->gateway.consultar("x".repeat(101),""));
    }
}
