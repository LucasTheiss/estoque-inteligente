package br.com.estoque.repository;

import br.com.estoque.model.Loja;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class LojaRepository {
    private final JdbcTemplate jdbc;
    private static final RowMapper<Loja> MAP =
            (r, n) ->
                    new Loja(
                            r.getLong("id"),
                            r.getString("slug"),
                            r.getString("name"),
                            r.getBoolean("iot"),
                            r.getBoolean("enterprise"),
                            r.getBoolean("fractional"),
                            r.getString("mqtt_host"),
                            r.getInt("mqtt_port"),
                            r.getInt("mqtt_qos"),
                            r.getInt("led_seconds"));

    public LojaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Loja> listar() {
        return jdbc.query("SELECT * FROM tenants WHERE active ORDER BY name", MAP);
    }

    public boolean ativa(long id) {
        return Boolean.TRUE.equals(
                jdbc.queryForObject(
                        "SELECT EXISTS(SELECT 1 FROM tenants WHERE id=? AND active)",
                        Boolean.class,
                        id));
    }

    public void desativar(long id) {
        jdbc.update("UPDATE tenants SET active=false WHERE id=?", id);
    }

    public Optional<Loja> porSlug(String slug) {
        return jdbc.query("SELECT * FROM tenants WHERE slug=?", MAP, slug).stream().findFirst();
    }

    public Loja buscar(long id) {
        return obter(id, "");
    }

    public Loja bloquear(long id) {
        return obter(id, " FOR UPDATE");
    }

    public Loja compartilhar(long id) {
        return obter(id, " FOR SHARE");
    }

    private Loja obter(long id, String lock) {
        return jdbc.query("SELECT * FROM tenants WHERE id=? AND active" + lock, MAP, id).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada."));
    }

    public long criar(
            String slug, String nome, boolean iot, boolean enterprise, boolean fractional) {
        return jdbc.queryForObject(
                "INSERT INTO tenants(slug,name,iot,enterprise,fractional) VALUES(?,?,?,?,?) RETURNING id",
                Long.class,
                slug,
                nome,
                iot,
                enterprise,
                fractional);
    }

    public void atualizar(
            long id, String nome, boolean iot, boolean enterprise, boolean fractional) {
        jdbc.update(
                "UPDATE tenants SET name=?,iot=?,enterprise=?,fractional=? WHERE id=?",
                nome,
                iot,
                enterprise,
                fractional,
                id);
    }

    public void configurarIoT(long id, String host, int port, int qos, int seconds) {
        jdbc.update(
                "UPDATE tenants SET mqtt_host=?,mqtt_port=?,mqtt_qos=?,led_seconds=? WHERE id=?",
                host,
                port,
                qos,
                seconds,
                id);
    }

    public void bloquearProvisionamento() {
        jdbc.queryForList("SELECT pg_advisory_xact_lock(9238174)");
    }
}
