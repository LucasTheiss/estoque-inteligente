package br.com.estoque.integration;

import br.com.estoque.model.Loja;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MqttGatewayTest {
    private Loja loja(String host) { return new Loja(1L,"teste","Teste",true,true,true,host,1883,1,10); }
    @Test void perfilLocalTraduzSomenteONomeInternoDoCompose() {
        var gateway=new MqttGateway(new ObjectMapper(),"127.0.0.1");
        assertEquals("tcp://127.0.0.1:1883",gateway.uri(loja("mqtt")));
        assertEquals("tcp://192.168.1.40:1883",gateway.uri(loja("192.168.1.40")));
    }
    @Test void execucaoDockerPreservaONomeDoServico() {
        var gateway=new MqttGateway(new ObjectMapper(),"mqtt");
        assertEquals("tcp://mqtt:1883",gateway.uri(loja("mqtt")));
    }
}
