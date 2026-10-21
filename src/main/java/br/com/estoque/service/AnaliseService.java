package br.com.estoque.service;

import br.com.estoque.dto.TabelaConsulta;
import br.com.estoque.integration.FornecedorGateway;
import java.time.*;
import java.sql.ResultSetMetaData;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnaliseService {
    private final JdbcTemplate jdbc;
    private final Acesso acesso;
    private final FornecedorGateway fornecedores;
    public AnaliseService(JdbcTemplate jdbc, Acesso acesso, FornecedorGateway fornecedores) {
        this.jdbc = jdbc; this.acesso = acesso; this.fornecedores = fornecedores;
    }

    private TabelaConsulta tabela(String titulo, String sql, Object... args) {
        return jdbc.query(sql, rs -> {
            ResultSetMetaData meta = rs.getMetaData();
            var colunas = new ArrayList<String>();
            for (int c = 1; c <= meta.getColumnCount(); c++) colunas.add(meta.getColumnLabel(c));
            var linhas = new ArrayList<List<String>>();
            while (rs.next()) {
                var linha = new ArrayList<String>();
                for (int c = 1; c <= meta.getColumnCount(); c++) linha.add(Objects.toString(rs.getObject(c), "—"));
                linhas.add(linha);
            }
            return new TabelaConsulta(titulo, colunas, linhas);
        }, args);
    }

    public TabelaConsulta consultar(long loja, String busca) {
        acesso.exigir(loja, "consultar");
        if (busca == null || busca.length() > 100) throw new IllegalArgumentException("Filtro deve ter até 100 caracteres.");
        return new TabelaConsulta("Fornecedores externos - demonstração",
            List.of("Código", "Nome", "Documento", "E-mail", "Telefone"), fornecedores.consultar(busca).stream()
                .map(f -> List.of(f.codigo(), f.nome(), f.documento(), f.email(), f.telefone())).toList());
    }

    private static final String SALDOS = """
        WITH saldos AS (SELECT p.id,p.sku,p.unit,p.price,coalesce(sum(o.quantity),0) saldo
        FROM products p LEFT JOIN occupancy o ON o.tenant_id=p.tenant_id AND o.product_id=p.id
        WHERE p.tenant_id=? AND p.active GROUP BY p.id)
        """;

    public TabelaConsulta relatorio(long loja, String tipo, LocalDate inicio, LocalDate fim) {
        acesso.exigir(loja, "relatorios");
        acesso.recurso(loja, "reports");
        if (inicio == null || fim == null || inicio.isAfter(fim))
            throw new IllegalArgumentException("Informe um período válido.");
        return switch (tipo) {
            case "valor" -> tabela("Valor potencial do estoque ao preço cadastrado", SALDOS + """
                SELECT sku AS "SKU",unit AS "Unidade",saldo AS "Saldo",price AS "Preço (R$)",
                round(saldo*price,2) AS "Valor (R$)",sum(round(saldo*price,2)) OVER () AS "Total da loja (R$)"
                FROM saldos ORDER BY sku
                """, loja);
            case "fluxo" -> tabela("Movimentações por produto e tipo no período", """
                SELECT p.sku AS "SKU",p.unit AS "Unidade",v.kind AS "Tipo",count(*) AS "Operações",
                sum(v.delta) AS "Variação líquida"
                FROM movements v JOIN products p ON p.tenant_id=v.tenant_id AND p.id=v.product_id
                WHERE v.tenant_id=? AND v.created_at>=? AND v.created_at<?
                GROUP BY p.sku,p.unit,v.kind ORDER BY p.sku,v.kind
                """, loja, inicio.atStartOfDay(ZoneId.of("America/Sao_Paulo")).toOffsetDateTime(),
                fim.plusDays(1).atStartOfDay(ZoneId.of("America/Sao_Paulo")).toOffsetDateTime());
            default -> throw new IllegalArgumentException("Relatório inválido.");
        };
    }
}
