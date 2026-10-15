package br.com.estoque.repository;

import br.com.estoque.model.ModeloProduto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ModeloProdutoRepository {
    private final JdbcTemplate jdbc;

    public ModeloProdutoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final org.springframework.jdbc.core.RowMapper<ModeloProduto> mapa =
            (r, n) ->
                    new ModeloProduto(
                            r.getLong("id"),
                            r.getLong("tenant_id"),
                            r.getString("name"),
                            r.getString("brand"),
                            r.getString("category"));

    public java.util.List<ModeloProduto> listar(long loja) {
        return jdbc.query(
                "SELECT * FROM models WHERE tenant_id=? ORDER BY name,brand,category", mapa, loja);
    }

    public ModeloProduto buscar(long loja, long id) {
        return obter(loja, id, "");
    }

    public ModeloProduto bloquear(long loja, long id) {
        return obter(loja, id, " FOR UPDATE");
    }

    private ModeloProduto obter(long loja, long id, String lock) {
        return jdbc
                .query("SELECT * FROM models WHERE tenant_id=? AND id=?" + lock, mapa, loja, id)
                .stream()
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("Modelo não encontrado nesta loja."));
    }

    public long salvar(ModeloProduto m) {
        if (m.id() == null)
            return jdbc.queryForObject(
                    "INSERT INTO models(tenant_id,name,brand,category) VALUES(?,?,?,?) RETURNING id",
                    Long.class,
                    m.tenantId(),
                    m.name(),
                    m.brand(),
                    m.category());
        jdbc.update(
                "UPDATE models SET name=?,brand=?,category=? WHERE tenant_id=? AND id=?",
                m.name(),
                m.brand(),
                m.category(),
                m.tenantId(),
                m.id());
        return m.id();
    }

    public boolean emUso(long loja, long id) {
        return Boolean.TRUE.equals(
                jdbc.queryForObject(
                        "SELECT EXISTS(SELECT 1 FROM products WHERE tenant_id=? AND model_id=?)",
                        Boolean.class,
                        loja,
                        id));
    }

    public void excluir(long loja, long id) {
        jdbc.update("DELETE FROM models WHERE tenant_id=? AND id=?", loja, id);
    }

    public ModeloProduto obterOuCriar(long loja, String nome, String marca, String categoria) {
        return jdbc.queryForObject(
                "INSERT INTO models(tenant_id,name,brand,category) VALUES(?,?,?,?) ON CONFLICT(tenant_id,name,brand,category) DO UPDATE SET name=EXCLUDED.name RETURNING *",
                (r, n) ->
                        new ModeloProduto(
                                r.getLong("id"),
                                r.getLong("tenant_id"),
                                r.getString("name"),
                                r.getString("brand"),
                                r.getString("category")),
                loja,
                nome,
                marca,
                categoria);
    }
}
