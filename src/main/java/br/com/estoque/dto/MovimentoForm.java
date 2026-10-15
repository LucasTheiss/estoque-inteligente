package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record MovimentoForm(
        long productId,
        long positionId,
        String kind,
        String quantity,
        String reason,
        String requestKey) {}
