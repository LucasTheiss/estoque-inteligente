package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record IoTForm(String mqttHost, Integer mqttPort, Integer mqttQos, Integer ledSeconds) {}
