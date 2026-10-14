package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record UsuarioForm(Long id, String username, String role, String password, String action) {}
