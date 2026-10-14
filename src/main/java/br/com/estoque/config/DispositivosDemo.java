package br.com.estoque.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.*;

/** Dispositivo virtual Java: exercita o broker real, sem afirmar acionamento físico. */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name="app.demo-iot",havingValue="true")
public class DispositivosDemo {
    private final ObjectMapper json;
    private final Map<String,byte[]> confirmacoes=new ConcurrentHashMap<>();
    private MqttClient client;
    @Value("${app.mqtt-user}") String usuario;
    @Value("${app.mqtt-password}") String senha;
    @Value("${app.demo-mqtt-uri:tcp://mqtt:1883}") String uri;
    public DispositivosDemo(ObjectMapper json){this.json=json;}
    @Scheduled(initialDelay=5000,fixedDelay=2000)
    public void publicar() {
        try {
            if(client==null) {
                client=new MqttClient(uri,"estoque-demo-"+java.util.UUID.randomUUID(),new MemoryPersistence());
                client.setTimeToWait(1500);
                client.setCallback(new MqttCallback() {
                    public void connectionLost(Throwable cause){}
                    public void deliveryComplete(IMqttDeliveryToken token){}
                    public void messageArrived(String topic,MqttMessage message)throws Exception {
                        if(message.isRetained() || message.getPayload().length>2048)return;
                        var command=json.readTree(message.getPayload());
                        confirmacoes.put(topic.replace("/led","/estado"),json.writeValueAsBytes(Map.of("simulated",true,"commandId",command.path("commandId").asText(),"position",command.path("position").asLong(),"pin",command.path("pin").asInt(),"state","ON")));
                    }
                });
            }
            if(!client.isConnected()) {
                var options=new MqttConnectOptions();options.setConnectionTimeout(1);options.setUserName(usuario);options.setPassword(senha.toCharArray());
                client.connect(options);
                for(String loja:new String[]{"calcados","granel"})client.subscribe("lojas/"+loja+"/controladores/esp32-demo/led",1);
            }
            client.publish("lojas/granel/balancas/balanca-demo/peso",json.writeValueAsBytes(Map.of("gross","1.440","tare","0.015","simulated",true)),0,false);
            for(var ack:confirmacoes.entrySet()) {
                client.publish(ack.getKey(),ack.getValue(),0,false);
                confirmacoes.remove(ack.getKey(),ack.getValue());
            }
        }catch(Exception ex){org.slf4j.LoggerFactory.getLogger(getClass()).debug("Broker de demonstração indisponível; nova tentativa no próximo ciclo.",ex);}
    }
    @PreDestroy public void fechar(){if(client!=null)try{client.close(true);}catch(Exception ignored){}}
}
