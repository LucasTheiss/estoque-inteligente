package br.com.estoque.model;

import java.math.BigDecimal;

public record Posicao(
        Long id,
        long tenantId,
        String code,
        String sector,
        int rowNo,
        int colNo,
        BigDecimal capacity,
        String unit,
        String device,
        Integer pin,
        boolean active) {}
