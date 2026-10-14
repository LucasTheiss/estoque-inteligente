package br.com.estoque.service;

import br.com.estoque.dto.*;
import br.com.estoque.model.*;
import br.com.estoque.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {
    final ProdutoRepository produtos=mock(ProdutoRepository.class);
    final ModeloProdutoRepository modelos=mock(ModeloProdutoRepository.class);
    final Acesso acesso=mock(Acesso.class);
    final VinculoService vinculos=mock(VinculoService.class);
    final EstoqueService estoque=mock(EstoqueService.class);
    final ProdutoService service=new ProdutoService(produtos,modelos,mock(OcupacaoRepository.class),
        mock(AuditoriaRepository.class),acesso,new ObjectMapper(),vinculos,estoque);

    @BeforeEach void preparar() {
        when(modelos.obterOuCriar(anyLong(),anyString(),anyString(),anyString()))
            .thenReturn(new ModeloProduto(1L,1L,"Tênis","",""));
        when(produtos.salvar(any())).thenReturn(11L,12L);
        when(acesso.usuario()).thenReturn(new Usuario(1L,1L,"admin","hash","admin",1,true));
    }
    ProdutoForm grade(String unidade,Long posicao,String saldos) {
        return new ProdutoForm(null,"Tênis","","","TENIS","","","",unidade,"10.00","{}","grade","36,37",posicao,saldos);
    }
    @Test void criaVariantesVinculaZeroEMovimentaApenasSaldoPositivo() {
        service.salvar(1,grade("un",3L,"36=2\n37=0"));
        var variantes=ArgumentCaptor.forClass(Produto.class);
        verify(produtos,times(2)).salvar(variantes.capture());
        assertEquals(java.util.List.of("TENIS-36","TENIS-37"),variantes.getAllValues().stream().map(Produto::sku).toList());
        verify(vinculos).salvar(1,11,3,false);
        verify(vinculos).salvar(1,12,3,false);
        var movimento=ArgumentCaptor.forClass(MovimentoForm.class);
        verify(estoque).movimentar(eq(1L),movimento.capture());
        assertEquals(11,movimento.getValue().productId());
        assertEquals("2",movimento.getValue().quantity());
        assertEquals("entrada",movimento.getValue().kind());
        verify(acesso).exigir(1,"entrada");
    }
    @Test void aceitaVirgulaDecimalPreservandoPrecisao() {
        service.salvar(1,grade("kg",3L,"36=1,125\r\n37=0"));
        var movimento=ArgumentCaptor.forClass(MovimentoForm.class);
        verify(estoque).movimentar(eq(1L),movimento.capture());
        assertEquals(new BigDecimal("1.125"),new BigDecimal(movimento.getValue().quantity()));
    }
    @ParameterizedTest
    @ValueSource(strings={"36=1","36=1\n36=2","36=1\n38=2","36=-1\n37=0","36=1.5\n37=0","36:1\n37=0"})
    void rejeitaSaldoIncompletoDuplicadoDesconhecidoOuInvalido(String texto) {
        assertThrows(IllegalArgumentException.class,()->service.salvar(1,grade("un",3L,texto)));
        verifyNoInteractions(produtos,vinculos,estoque);
    }
    @Test void saldoExigeDestinoEPermissaoDeEntrada() {
        assertThrows(IllegalArgumentException.class,()->service.salvar(1,grade("un",null,"36=1\n37=0")));
        doThrow(new org.springframework.security.access.AccessDeniedException("Sem entrada")).when(acesso).exigir(1,"entrada");
        assertThrows(org.springframework.security.access.AccessDeniedException.class,()->service.salvar(1,grade("un",3L,"36=1\n37=0")));
        verifyNoInteractions(produtos,vinculos,estoque);
    }
    @Test void cadastroSemSaldoPreservaFluxoAnterior() {
        service.salvar(1,grade("un",null,""));
        verify(produtos,times(2)).salvar(any());
        verifyNoInteractions(vinculos,estoque);
        verify(acesso,never()).exigir(1,"entrada");
    }
}
