package br.com.estoque.view;

import br.com.estoque.dto.MovimentacaoResumo;
import java.io.*;
import java.util.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.springframework.stereotype.Component;

@Component
public class RelatorioPdf extends RelatorioTemplate {
    public RelatorioPdf() { super("application/pdf","historico.pdf"); }
    @Override protected byte[] renderizarConteudo(long loja,List<MovimentacaoResumo> registros) throws IOException { return renderizar(loja,registros); }
    public byte[] renderizar(long loja, List<MovimentacaoResumo> registros) throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("Estoque Inteligente — Histórico da loja " + loja);
        linhas.add("");
        for (var r : registros) {
            var m = r.movimento();
            linhas.add(m.id() + " | " + r.sku() + " | " + m.kind() + " | " + m.delta() + " | " + r.code());
            linhas.add("Data: " + m.createdAt() + " | Responsável: " + r.username());
            linhas.add("Motivo: " + m.reason());
            linhas.add("");
        }
        try (var doc = new PDDocument(); var out = new ByteArrayOutputStream()) {
            var fonte = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            List<String> quebradas = new ArrayList<>();
            // ponytail: fonte padrão PDF limitada a Latin-1; incorporar Unicode quando houver outros alfabetos.
            for (String linha : linhas) {
                String texto = linha.replaceAll("[^\\x20-\\x7E\\u00A0-\\u00FF]", "?");
                if (texto.isEmpty()) quebradas.add("");
                while (!texto.isEmpty()) {
                    int corte = Math.min(85, texto.length());
                    quebradas.add(texto.substring(0, corte)); texto = texto.substring(corte);
                }
            }
            for (int i = 0; i < quebradas.size(); i += 49) {
                var pagina = new PDPage(PDRectangle.A4); doc.addPage(pagina);
                try (var stream = new PDPageContentStream(doc, pagina)) {
                    stream.beginText(); stream.setFont(fonte, 10);
                    stream.newLineAtOffset(40, 790); stream.setLeading(15);
                    for (String linha : quebradas.subList(i, Math.min(i + 49, quebradas.size()))) {
                        stream.showText(linha); stream.newLine();
                    }
                    stream.endText();
                }
            }
            doc.save(out); return out.toByteArray();
        }
    }
}
