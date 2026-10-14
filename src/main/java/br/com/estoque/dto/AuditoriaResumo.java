package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record AuditoriaResumo(RegistroAuditoria registro, String username) {}
