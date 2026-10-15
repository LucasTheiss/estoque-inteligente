package br.com.estoque.view;

import br.com.estoque.dto.MovimentacaoResumo;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RelatorioCsv extends RelatorioTemplate {
    public RelatorioCsv() { super("text/csv;charset=UTF-8","historico.csv"); }
    @Override protected byte[] renderizarConteudo(long loja,List<MovimentacaoResumo> registros) { return renderizar(registros); }
    public byte[] renderizar(List<MovimentacaoResumo> registros) {
        StringBuilder csv = new StringBuilder("\ufeffID;Data;SKU;Tipo;Posição;Variação;Responsável;Motivo\r\n");
        for (var r : registros) {
            var m = r.movimento();
            var campos = List.of(m.id().toString(), m.createdAt().toString(), r.sku(),
                    m.kind(), r.code(), m.delta().toPlainString(), r.username(), m.reason());
            csv.append(String.join(";", campos.stream().map(RelatorioCsv::escapar).toList())).append("\r\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String escapar(String valor) {
        if (valor.stripLeading().matches("^[=+@-].*")) valor = "'" + valor;
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }
}
