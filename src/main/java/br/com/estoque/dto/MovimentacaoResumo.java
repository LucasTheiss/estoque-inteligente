package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record MovimentacaoResumo(
        Movimentacao movimento,
        String sku,
        String name,
        String code,
        String username,
        boolean estornado) {}
