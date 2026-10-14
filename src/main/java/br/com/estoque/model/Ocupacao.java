package br.com.estoque.model;

import java.math.BigDecimal;

public record Ocupacao(long tenantId, long productId, long positionId, BigDecimal quantity) {}
