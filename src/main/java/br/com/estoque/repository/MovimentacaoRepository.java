package br.com.estoque.repository;

import br.com.estoque.model.Movimentacao;
import br.com.estoque.dto.MovimentacaoResumo;
import java.sql.*;
import java.time.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MovimentacaoRepository {
    private final JdbcTemplate jdbc;

    public MovimentacaoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Movimentacao ler(ResultSet r) throws SQLException {
        return new Movimentacao(
                r.getLong("id"),
                r.getLong("tenant_id"),
                r.getLong("product_id"),
                r.getLong("position_id"),
                r.getLong("actor_id"),
                r.getString("kind"),
                r.getBigDecimal("delta"),
                r.getBigDecimal("before_qty"),
                r.getBigDecimal("after_qty"),
                r.getString("reason"),
                r.getObject("created_at", OffsetDateTime.class),
                r.getString("request_key"),
                r.getString("fingerprint"),
                r.getObject("reverse_of", Long.class));
    }

    public void bloquearChave(long loja, String chave) {
        jdbc.queryForList(
                "SELECT pg_advisory_xact_lock(hashtextextended(?,0))", loja + ":" + chave);
    }

    public Optional<Movimentacao> porChave(long loja, String chave) {
        return jdbc
                .query(
                        "SELECT * FROM movements WHERE tenant_id=? AND request_key=?",
                        (r, n) -> ler(r),
                        loja,
                        chave)
                .stream()
                .findFirst();
    }

    public Movimentacao buscar(long loja, long id) {
        return obter(loja, id, "");
    }

    public Movimentacao bloquear(long loja, long id) {
        return obter(loja, id, " FOR UPDATE");
    }

    private Movimentacao obter(long loja, long id, String lock) {
        return jdbc
                .query(
                        "SELECT * FROM movements WHERE tenant_id=? AND id=?" + lock,
                        (r, n) -> ler(r),
                        loja,
                        id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Movimentação não encontrada."));
    }

    public boolean estornada(long loja, long id) {
        return Boolean.TRUE.equals(
                jdbc.queryForObject(
                        "SELECT EXISTS(SELECT 1 FROM movements WHERE tenant_id=? AND reverse_of=?)",
                        Boolean.class,
                        loja,
                        id));
    }

    public Movimentacao registrar(Movimentacao m) {
        return jdbc.queryForObject(
                "INSERT INTO movements(tenant_id,product_id,position_id,actor_id,kind,delta,before_qty,after_qty,reason,request_key,fingerprint,reverse_of) VALUES(?,?,?,?,?,?,?,?,?,?,?,?) RETURNING *",
                (r, n) -> ler(r),
                m.tenantId(),
                m.productId(),
                m.positionId(),
                m.actorId(),
                m.kind(),
                m.delta(),
                m.beforeQty(),
                m.afterQty(),
                m.reason(),
                m.requestKey(),
                m.fingerprint(),
                m.reverseOf());
    }

    public List<MovimentacaoResumo> historico(
            long loja, String tipo, LocalDate inicio, LocalDate fim) {
        String sql =
                "SELECT v.*,p.sku,m.name,s.code,u.username,EXISTS(SELECT 1 FROM movements e WHERE e.tenant_id=v.tenant_id AND e.reverse_of=v.id) estornado FROM movements v JOIN products p ON p.id=v.product_id JOIN models m ON m.id=p.model_id JOIN positions s ON s.id=v.position_id JOIN users u ON u.id=v.actor_id WHERE v.tenant_id=?";
        List<Object> args = new ArrayList<>(List.of(loja));
        if (!tipo.isBlank()) {
            sql += " AND v.kind=?";
            args.add(tipo);
        }
        if (inicio != null) {
            sql += " AND v.created_at>=?";
            args.add(inicio.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime());
        }
        if (fim != null) {
            sql += " AND v.created_at<?";
            args.add(fim.plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime());
        }
        // ponytail: 5.000 registros por exportação; paginar por cursor para consultas maiores.
        return jdbc.query(
                sql + " ORDER BY v.id DESC LIMIT 5000",
                (r, n) ->
                        new MovimentacaoResumo(
                                ler(r),
                                r.getString("sku"),
                                r.getString("name"),
                                r.getString("code"),
                                r.getString("username"),
                                r.getBoolean("estornado")),
                args.toArray());
    }
}
