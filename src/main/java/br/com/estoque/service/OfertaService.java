package br.com.estoque.service;

import br.com.estoque.dto.TabelaConsulta;
import br.com.estoque.integration.OfertaGateway;
import java.math.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OfertaService {
    private final OfertaGateway ofertas;
    private final JdbcTemplate jdbc;
    private final Acesso acesso;
    public OfertaService(OfertaGateway ofertas, JdbcTemplate jdbc, Acesso acesso) {
        this.ofertas = ofertas; this.jdbc = jdbc; this.acesso = acesso;
    }
    public TabelaConsulta consultar(long loja, String gtin, String fornecedor) {
        acesso.exigir(loja, "consultar");
        return new TabelaConsulta("Ofertas externas", List.of("GTIN", "Fornecedor", "SKU externo", "Descrição",
                "Marca", "Unidade", "Preço cotado (R$)", "Disponibilidade", "Atualização"),
                ofertas.consultar(gtin, fornecedor).stream().map(o -> List.of(o.gtin(), o.fornecedor(), o.sku(),
                        o.descricao(), o.marca(), o.unidade(), o.preco().toPlainString(),
                        o.disponibilidade().toPlainString(), o.atualizacao())).toList());
    }
    public TabelaConsulta comparar(long loja, String gtin, String fornecedor) {
        acesso.exigir(loja, "relatorios"); acesso.recurso(loja, "reports");
        var externas = ofertas.consultar(gtin, fornecedor);
        record Preco(String sku, String unidade, BigDecimal valor) {}
        var internos = new HashMap<String, Preco>();
        jdbc.query("SELECT barcode,sku,unit,price FROM products WHERE tenant_id=? AND active AND barcode<>''",
                (org.springframework.jdbc.core.RowCallbackHandler) r -> internos.put(r.getString("barcode"),
                        new Preco(r.getString("sku"), r.getString("unit"), r.getBigDecimal("price"))), loja);
        var linhas = new ArrayList<List<String>>();
        for (var o : externas) {
            var p = internos.get(o.gtin());
            if (p == null || !p.unidade().equals(o.unidade())) continue;
            var diferenca = o.preco().subtract(p.valor()).setScale(2, RoundingMode.HALF_UP);
            String percentual = p.valor().signum() == 0 ? "Não aplicável (preço interno zero)"
                    : diferenca.multiply(new BigDecimal("100")).divide(p.valor(), 2, RoundingMode.HALF_UP).toPlainString();
            linhas.add(List.of(o.gtin(), p.sku(), o.fornecedor(), o.sku(), o.unidade(), p.valor().toPlainString(),
                    o.preco().toPlainString(), diferenca.toPlainString(), percentual, o.atualizacao()));
        }
        return new TabelaConsulta("Comparação de preços", List.of("GTIN", "SKU interno", "Fornecedor", "SKU externo",
                "Unidade", "Preço interno (R$)", "Cotação (R$)", "Diferença (R$)", "Diferença (%)", "Atualização"), linhas);
    }
}
