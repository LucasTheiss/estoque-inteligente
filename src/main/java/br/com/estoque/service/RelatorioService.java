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
    private final RelatorioCsv csv;
    private final RelatorioPdf pdf;
    public RelatorioService(Acesso acesso, HistoricoService historico, AuditoriaRepository auditoria,
                            RelatorioCsv csv, RelatorioPdf pdf) {
        this.acesso=acesso; this.historico=historico; this.auditoria=auditoria; this.csv=csv; this.pdf=pdf;
    }
    public RelatorioArquivo exportar(long loja, String formato, String tipo, String inicio, String fim) throws IOException {
        acesso.exigir(loja,"relatorios"); acesso.recurso(loja,"enterprise");
        var registros=historico.filtrar(loja,tipo,inicio,fim);
        var arquivo=switch(formato) {
            case "csv" -> new RelatorioArquivo(csv.renderizar(registros),"text/csv;charset=UTF-8","historico.csv");
            case "pdf" -> new RelatorioArquivo(pdf.renderizar(loja,registros),"application/pdf","historico.pdf");
            default -> throw new IllegalArgumentException("Formato inválido. Escolha CSV ou PDF.");
        };
        auditoria.registrar(loja,acesso.usuario().id(),"historico.exportar",formato+"; "+registros.size()+" registros");
        return arquivo;
    }
}
