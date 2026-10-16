package br.com.estoque.dto;

import br.com.estoque.model.*;
import java.math.BigDecimal;

public record LojaForm(
        Long id,
        String slug,
        String name,
        Boolean iot,
        Boolean reports,
        Boolean occupancy,
        Boolean fractional,
        String username,
        String password) {
    public LojaForm {
        // Checkboxes desmarcados não são enviados pelo navegador.
        iot = Boolean.TRUE.equals(iot);
        reports = Boolean.TRUE.equals(reports);
        occupancy = Boolean.TRUE.equals(occupancy);
        fractional = Boolean.TRUE.equals(fractional);
    }
}
