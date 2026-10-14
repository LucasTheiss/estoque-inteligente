package br.com.estoque.model;

import java.time.OffsetDateTime;

public record TentativaLogin(String key, int attempts, OffsetDateTime expiresAt) {}
