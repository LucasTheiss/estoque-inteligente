package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record VinculoResumo(Ocupacao ocupacao, String sku,String barcode, String name, String size, String color, String code, String sector, String unit, String device, Integer pin) {}
