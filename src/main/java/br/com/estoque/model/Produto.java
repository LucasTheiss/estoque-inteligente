package br.com.estoque.model;

import java.math.BigDecimal;

public record Produto(Long id, long tenantId, long modelId, String sku, String barcode, String color, String size, String unit, BigDecimal price, String attributes, boolean active) {}
