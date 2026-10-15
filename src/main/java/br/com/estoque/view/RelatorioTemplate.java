package br.com.estoque.view;

import br.com.estoque.dto.*;
import br.com.estoque.strategy.FormatoRelatorio;
import java.io.IOException;
import java.util.List;

/** Template Method: validar, fixar os registros, renderizar e montar o arquivo. */
public abstract class RelatorioTemplate implements FormatoRelatorio {
    private final String tipo;
    private final String nome;

    protected RelatorioTemplate(String tipo, String nome) {
        this.tipo = tipo;
        this.nome = nome;
    }

    @Override
    public final RelatorioArquivo gerar(long loja, List<MovimentacaoResumo> registros)
            throws IOException {
        if (loja <= 0 || registros == null)
            throw new IllegalArgumentException("Informe uma loja e os registros do relatório.");
        var copia = List.copyOf(registros);
        if (copia.stream().anyMatch(r -> r.movimento().tenantId() != loja))
            throw new IllegalArgumentException("Relatório contém registros de outra loja.");
        byte[] conteudo = renderizarConteudo(loja, copia);
        return new RelatorioArquivo(conteudo, tipo, nome);
    }

    protected abstract byte[] renderizarConteudo(long loja, List<MovimentacaoResumo> registros)
            throws IOException;
}
