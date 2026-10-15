package br.com.estoque.strategy;

import br.com.estoque.dto.*;
import java.io.IOException;
import java.util.List;

/** Strategy: o serviço exporta pelo contrato, sem conhecer o renderizador escolhido. */
public interface FormatoRelatorio {
    RelatorioArquivo gerar(long loja, List<MovimentacaoResumo> registros) throws IOException;
}
