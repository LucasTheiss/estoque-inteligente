package br.com.estoque.service;

import br.com.estoque.dto.RelatorioArquivo;
import br.com.estoque.repository.AuditoriaRepository;
import br.com.estoque.view.*;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {
    private final Acesso acesso;
    private final HistoricoService historico;
    private final AuditoriaRepository auditoria;
    private final java.util.Map<String, br.com.estoque.strategy.FormatoRelatorio> formatos;

    public RelatorioService(
            Acesso acesso,
            HistoricoService historico,
            AuditoriaRepository auditoria,
            RelatorioCsv csv,
            RelatorioPdf pdf) {
        this.acesso = acesso;
        this.historico = historico;
        this.auditoria = auditoria;
        this.formatos = java.util.Map.of("csv", csv, "pdf", pdf);
    }

    public RelatorioArquivo exportar(
            long loja, String formato, String tipo, String inicio, String fim) throws IOException {
        acesso.exigir(loja, "relatorios");
        acesso.recurso(loja, "enterprise");
        var registros = historico.filtrar(loja, tipo, inicio, fim);
        var estrategia = formatos.get(formato);
        if (estrategia == null)
            throw new IllegalArgumentException("Formato inválido. Escolha CSV ou PDF.");
        var arquivo = estrategia.gerar(loja, registros);
        auditoria.registrar(
                loja,
                acesso.usuario().id(),
                "historico.exportar",
                formato + "; " + registros.size() + " registros");
        return arquivo;
    }
}
