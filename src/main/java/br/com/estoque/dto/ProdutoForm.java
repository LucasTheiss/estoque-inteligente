package br.com.estoque.dto;

public record ProdutoForm(
        Long id,
        String name,
        String brand,
        String category,
        String sku,
        String barcode,
        String color,
        String size,
        String unit,
        String price,
        String attributes,
        String structure,
        String sizes,
        Long initialPositionId,
        String initialQuantities) {}
