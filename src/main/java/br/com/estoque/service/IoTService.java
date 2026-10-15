package br.com.estoque.service;

import br.com.estoque.integration.MqttGateway;
import br.com.estoque.repository.*;
import br.com.estoque.strategy.Quantidade;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.math.*;
import org.springframework.stereotype.Service;

@Service
public class IoTService {
    private final LojaRepository lojas;
    private final PosicaoRepository posicoes;
    private final ProdutoRepository produtos;
    private final AuditoriaRepository auditoria;
    private final Acesso acesso;
    private final MqttGateway mqtt;
    private final Map<String, String> pendentes = new ConcurrentHashMap<>();

    public IoTService(
            LojaRepository lojas,
            PosicaoRepository posicoes,
            ProdutoRepository produtos,
            AuditoriaRepository auditoria,
            Acesso acesso,
            MqttGateway mqtt) {
        this.lojas = lojas;
        this.posicoes = posicoes;
        this.produtos = produtos;
        this.auditoria = auditoria;
        this.acesso = acesso;
        this.mqtt = mqtt;
    }

    public Map<String, Object> status(long loja) {
        acesso.exigir(loja, "consultar");
        acesso.recurso(loja, "iot");
        return Map.of("connected", mqtt.conectado(lojas.buscar(loja)));
    }

    public Map<String, Object> sinalizar(long loja, long posicao) {
        acesso.exigir(loja, "consultar");
        acesso.recurso(loja, "iot");
        var p = posicoes.buscar(loja, posicao);
        var tenant = lojas.buscar(loja);
        if (p.device().isBlank() || p.pin() == null)
            throw new IllegalArgumentException("Configure o controlador e o pino desta posição.");
        String topic = "lojas/" + tenant.slug() + "/controladores/" + p.device() + "/led";
        String comando = UUID.randomUUID().toString();
        var payload =
                Map.of(
                        "action",
                        "ON",
                        "pin",
                        p.pin(),
                        "position",
                        posicao,
                        "timeout",
                        tenant.ledSeconds(),
                        "commandId",
                        comando);
        mqtt.publicar(tenant, topic, payload);
        if (pendentes.size() > 1000) pendentes.clear();
        pendentes.put(loja + ":" + posicao, comando);
        auditoria.registrar(loja, acesso.usuario().id(), "iot.sinalizar", String.valueOf(posicao));
        return Map.of(
                "status",
                "Comando enviado ao broker. Aguarde a confirmação do dispositivo.",
                "commandId",
                comando);
    }

    public Map<String, Object> peso(long loja, long produto, String device) {
        acesso.exigir(loja, "saida");
        acesso.recurso(loja, "iot");
        acesso.recurso(loja, "fractional");
        if (!device.matches("[a-zA-Z0-9_-]{1,200}"))
            throw new IllegalArgumentException("Dispositivo inválido.");
        var p = produtos.buscar(loja, produto);
        var tenant = lojas.buscar(loja);
        if (!p.unit().equals("kg"))
            throw new IllegalArgumentException("Selecione um produto vendido por peso.");
        if (!mqtt.conectado(tenant))
            throw new IllegalArgumentException("MQTT indisponível. Confira a conexão.");
        var leitura =
                mqtt.leitura("lojas/" + tenant.slug() + "/balancas/" + device + "/peso")
                        .filter(l -> l.recente(5))
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Sem leitura recente da balança. Confira a conexão."));
        BigDecimal bruto = Quantidade.decimal(leitura.dados().path("gross").asText(), 3),
                tara = Quantidade.decimal(leitura.dados().path("tare").asText(), 3),
                liquido = bruto.subtract(tara);
        if (liquido.signum() <= 0) throw new IllegalArgumentException("Peso líquido inválido.");
        return Map.of(
                "quantity",
                liquido,
                "gross",
                bruto,
                "tare",
                tara,
                "simulated",
                leitura.dados().path("simulated").asBoolean(false),
                "subtotal",
                liquido.multiply(p.price()).setScale(2, RoundingMode.HALF_UP));
    }

    public Map<String, Object> confirmacao(long loja, long posicao) {
        acesso.exigir(loja, "consultar");
        acesso.recurso(loja, "iot");
        var p = posicoes.buscar(loja, posicao);
        var tenant = lojas.buscar(loja);
        var leitura =
                mqtt.leitura("lojas/" + tenant.slug() + "/controladores/" + p.device() + "/estado")
                        .filter(l -> l.recente(10))
                        .filter(l -> l.dados().path("position").asLong(-1) == posicao)
                        .filter(
                                l ->
                                        Objects.equals(
                                                pendentes.get(loja + ":" + posicao),
                                                l.dados().path("commandId").asText()))
                        .orElse(null);
        if (leitura == null) return Map.of("confirmed", false);
        return Map.of(
                "confirmed",
                true,
                "simulated",
                leitura.dados().path("simulated").asBoolean(false),
                "state",
                leitura.dados().path("state").asText());
    }
}
