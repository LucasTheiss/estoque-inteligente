package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record LojaForm(Long id, String slug, String name, boolean iot, boolean enterprise, boolean fractional, String username, String password) {}
