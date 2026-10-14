package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record PosicaoResumo(Posicao posicao, BigDecimal quantidade) {}
