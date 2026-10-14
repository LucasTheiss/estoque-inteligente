package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record PosicaoForm(Long id, String code, String sector, Integer rowNo, Integer colNo, String capacity, String unit, String device, Integer pin) {}
