package br.com.estoque.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Movimentacao(
        Long id,
        long tenantId,
        long productId,
        long positionId,
        long actorId,
        String kind,
        BigDecimal delta,
        BigDecimal beforeQty,
        BigDecimal afterQty,
        String reason,
        OffsetDateTime createdAt,
        String requestKey,
        String fingerprint,
        Long reverseOf) {}
