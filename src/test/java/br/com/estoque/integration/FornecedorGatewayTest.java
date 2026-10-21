package br.com.estoque.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class FornecedorGatewayTest {
    HttpServer server;
    String resposta;
    int status;
    FornecedorGateway gateway;
    @BeforeEach void preparar() throws Exception {
        status=200;
        resposta="[{\"codigo\":\"F1\",\"nome\":\"Aurora\",\"documento\":\"DEMO\",\"email\":\"a@example.org\",\"telefone\":\"123\"}]";
        server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/fornecedores", e->{
            assertEquals("GET",e.getRequestMethod());
            byte[] body=resposta.getBytes(StandardCharsets.UTF_8);
            e.sendResponseHeaders(status,body.length);e.getResponseBody().write(body);e.close();
        });
        server.start();
        gateway=new FornecedorGateway("http://127.0.0.1:"+server.getAddress().getPort()+"/fornecedores",new ObjectMapper());
    }
    @AfterEach void parar(){server.stop(0);}
    @Test void consultaOrigemNovamenteEFiltra(){
        assertEquals(1,gateway.consultar("aurora").size());
        assertTrue(gateway.consultar("inexistente").isEmpty());
        resposta="[]";
        assertTrue(gateway.consultar("").isEmpty());
    }
    @Test void falhaNaoViraListaVazia(){
        status=503;
        assertThrows(IllegalArgumentException.class,()->gateway.consultar(""));
        status=200;resposta="[{\"codigo\":\"F1\"}]";
        assertThrows(IllegalArgumentException.class,()->gateway.consultar(""));
    }
}
