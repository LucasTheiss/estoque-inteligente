package br.com.estoque.integration;

import br.com.estoque.model.Loja;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MqttGateway {
    public record Leitura(long instante,JsonNode dados) {
        public boolean recente(long segundos) { return System.nanoTime()-instante<segundos*1_000_000_000L; }
    }
    private final ObjectMapper json;
    private final String hostDocker;
    private final Map<Long,MqttClient> clients=new ConcurrentHashMap<>();
    private final Map<String,Leitura> leituras=new ConcurrentHashMap<>();
    @Value("${app.mqtt-user}") String usuario;
    @Value("${app.mqtt-password}") String senha;
    public MqttGateway(ObjectMapper json,@Value("${app.mqtt-docker-host:mqtt}") String hostDocker) { this.json=json;this.hostDocker=hostDocker; }
    String uri(Loja loja) {
        String host=loja.mqttHost().equals("mqtt")?hostDocker:loja.mqttHost();
        return "tcp://"+host+":"+loja.mqttPort();
    }
    private void assinar(MqttClient client,Loja loja) throws MqttException {
        client.subscribe("lojas/"+loja.slug()+"/+/+/estado",1);
        client.subscribe("lojas/"+loja.slug()+"/balancas/+/peso",1);
    }
    // ponytail: criação de clientes sincronizada; usar locks por loja se conexões simultâneas virarem gargalo.
    private synchronized MqttClient cliente(Loja loja) throws MqttException {
        String uri=uri(loja);
        var client=clients.get(loja.id());
        if(client!=null && !client.getServerURI().equals(uri)) {client.close(true);clients.remove(loja.id());client=null;}
        if(client==null) {
            client=new MqttClient(uri,MqttClient.generateClientId(),new MemoryPersistence());client.setTimeToWait(1500);
            var atual=client;
            client.setCallback(new MqttCallbackExtended() {
                public void connectComplete(boolean reconnect,String serverURI) {if(reconnect)try{assinar(atual,loja);}catch(MqttException ignored){}}
                public void connectionLost(Throwable cause) {}
                public void deliveryComplete(IMqttDeliveryToken token) {}
                public void messageArrived(String topic,MqttMessage message) {
                    if(message.isRetained() || message.getPayload().length>2048)return;
                    try {
                        var dados=json.readTree(message.getPayload());
                        if(!dados.isObject())return;
                        // ponytail: cache limitado a 1.000 tópicos; substituir por LRU quando a instalação ultrapassar esse volume.
                        if(leituras.size()>=1000 && !leituras.containsKey(topic))leituras.clear();
                        leituras.put(topic,new Leitura(System.nanoTime(),dados));
                    }catch(Exception ex){org.slf4j.LoggerFactory.getLogger(MqttGateway.class).debug("Telemetria MQTT inválida descartada.");}
                }
            });clients.put(loja.id(),client);
        }
        if(!client.isConnected()) {
            var options=new MqttConnectOptions();options.setConnectionTimeout(1);options.setAutomaticReconnect(true);options.setKeepAliveInterval(30);
            if(!usuario.isBlank()){options.setUserName(usuario);options.setPassword(senha.toCharArray());}
            client.connect(options);assinar(client,loja);
        }
        return client;
    }
    public boolean conectado(Loja loja) {try{return cliente(loja).isConnected();}catch(Exception ex){return false;}}
    public void publicar(Loja loja,String topic,Object mensagem) {
        try{cliente(loja).publish(topic,json.writeValueAsBytes(mensagem),loja.mqttQos(),false);}
        catch(Exception ex){throw new IllegalArgumentException("MQTT indisponível. A localização continua disponível na tela.");}
    }
    public Optional<Leitura> leitura(String topic) {return Optional.ofNullable(leituras.get(topic));}
    @PreDestroy public void fechar(){clients.values().forEach(c->{try{c.close(true);}catch(Exception ignored){}});}
}
