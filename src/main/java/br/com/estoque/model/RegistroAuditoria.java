package br.com.estoque.model;

import java.time.OffsetDateTime;

public record RegistroAuditoria(Long id, Long tenantId, Long actorId, String action, String detail, OffsetDateTime createdAt) {}
