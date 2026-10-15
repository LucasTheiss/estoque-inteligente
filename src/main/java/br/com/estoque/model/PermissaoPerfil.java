package br.com.estoque.model;

public record PermissaoPerfil(long tenantId, String role, String permission) {}
