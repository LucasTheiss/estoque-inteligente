package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record LojaForm(Long id, String slug, String name, Boolean iot, Boolean enterprise, Boolean fractional, String username, String password) {
    public LojaForm {
        // Checkboxes desmarcados não são enviados pelo navegador.
        iot=Boolean.TRUE.equals(iot);
        enterprise=Boolean.TRUE.equals(enterprise);
        fractional=Boolean.TRUE.equals(fractional);
    }
}
