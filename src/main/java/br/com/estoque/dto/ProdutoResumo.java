package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record ProdutoResumo(Produto produto, ModeloProduto modelo, BigDecimal quantidade) {}
