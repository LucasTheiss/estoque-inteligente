package br.com.estoque.model;


public record ModeloProduto(Long id, long tenantId, String name, String brand, String category) {}
