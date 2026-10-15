package br.com.estoque.service;

import br.com.estoque.dto.IoTForm;
import br.com.estoque.model.Loja;
import br.com.estoque.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracaoIoTService {
    private final LojaRepository lojas;
    private final AuditoriaRepository auditoria;
    private final Acesso acesso;

    public ConfiguracaoIoTService(
            LojaRepository lojas, AuditoriaRepository auditoria, Acesso acesso) {
        this.lojas = lojas;
        this.auditoria = auditoria;
        this.acesso = acesso;
    }

    public Loja buscar(long loja) {
        acesso.exigir(loja, "iot");
        acesso.recurso(loja, "iot");
        return lojas.buscar(loja);
    }

    @Transactional
    public void salvar(long loja, IoTForm f) {
        acesso.exigir(loja, "iot");
        acesso.recurso(loja, "iot");
        String host = Campos.texto(f.mqttHost(), "host MQTT", true);
        if (!host.matches("[a-zA-Z0-9.:-]+")
                || f.mqttPort() == null
                || f.mqttPort() < 1
                || f.mqttPort() > 65535
                || f.mqttQos() == null
                || f.mqttQos() < 0
                || f.mqttQos() > 1
                || f.ledSeconds() == null
                || f.ledSeconds() < 5
                || f.ledSeconds() > 60)
            throw new IllegalArgumentException("Parâmetros MQTT inválidos.");
        lojas.configurarIoT(loja, host, f.mqttPort(), f.mqttQos(), f.ledSeconds());
        auditoria.registrar(loja, acesso.usuario().id(), "iot.configurar", host);
    }
}
