package br.com.estoque.strategy;

import br.com.estoque.domain.Produto;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PoliticaReposicaoTest {
    private final Produto baixo = new Produto("SKU-1", "Café", 3, 5, BigDecimal.TEN);

    @Test void politicasCalculamReposicoesDistintas() {
        assertEquals(2, new ReposicaoNoMinimo().calcular(baixo).orElseThrow().quantidade());
        assertEquals(10, new ReposicaoLoteFixo(10).calcular(baixo).orElseThrow().quantidade());
        assertEquals(7, new ReposicaoCobertura(2, 5).calcular(baixo).orElseThrow().quantidade());
        assertTrue(new ReposicaoNoMinimo().calcular(new Produto("SKU-2", "Leite", 5, 5, BigDecimal.ONE)).isEmpty());
    }
}
