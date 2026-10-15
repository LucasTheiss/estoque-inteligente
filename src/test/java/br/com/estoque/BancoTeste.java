package br.com.estoque;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;

/** Dados de preparação e inspeção exclusivos dos testes de integração. */
public class BancoTeste {
    private final JdbcTemplate jdbc;

    public BancoTeste(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> listar(String sql, Object... args) {
        return jdbc.queryForList(sql, args);
    }

    public Map<String, Object> um(String sql, Object... args) {
        var rows = listar(sql, args);
        if (rows.isEmpty()) throw new IllegalArgumentException("Registro não encontrado.");
        return rows.getFirst();
    }

    public int executar(String sql, Object... args) {
        return jdbc.update(sql, args);
    }

    public long id(String sql, Object... args) {
        return ((Number) um(sql, args).get("id")).longValue();
    }

    public void auditar(Long loja, long ator, String acao, String detalhe) {
        executar(
                "INSERT INTO audit(tenant_id,actor_id,action,detail) VALUES(?,?,?,jsonb_build_object('descricao',?::text))",
                loja,
                ator,
                acao,
                detalhe);
    }
}
