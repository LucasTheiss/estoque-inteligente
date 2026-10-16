package br.com.estoque.model;

public record Loja(
        Long id,
        String slug,
        String name,
        boolean iot,
        boolean reports,
        boolean occupancy,
        boolean fractional,
        String mqttHost,
        int mqttPort,
        int mqttQos,
        int ledSeconds) {}
