package br.com.estoque.service;

import br.com.estoque.strategy.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OperacaoEstoqueTest {
    @Test void templatePreservaSaldoEPermiteZerar() {
        var saldo=new BigDecimal("3.250");
        assertEquals(0,new AjusteEstoque().executar(saldo,BigDecimal.ZERO).signum());
        assertEquals(new BigDecimal("3.750"),new EntradaEstoque().executar(saldo,new BigDecimal("0.5")));
        assertEquals(new BigDecimal("2.750"),new SaidaEstoque().executar(saldo,new BigDecimal("0.5")));
        assertThrows(IllegalArgumentException.class,()->new SaidaEstoque().executar(saldo,BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class,()->new AjusteEstoque().executar(saldo,saldo));
        assertThrows(IllegalArgumentException.class,()->new EntradaEstoque().executar(saldo,new BigDecimal("-1")));
    }
    @Test void estrategiaValidaPrecisaoSemPontoFlutuante() {
        assertEquals(new BigDecimal("0.125"),new QuantidadeFracionada().validar("0.125"));
        assertThrows(IllegalArgumentException.class,()->new QuantidadeInteira().validar("0.5"));
        for(String invalido:new String[]{"NaN","Infinity","-1","0.0001","1000000000000"})
            assertThrows(IllegalArgumentException.class,()->new QuantidadeFracionada().validar(invalido));
        assertThrows(IllegalArgumentException.class,()->Campos.senha("é".repeat(37)));
    }
}
