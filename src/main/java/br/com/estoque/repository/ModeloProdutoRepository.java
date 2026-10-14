package br.com.estoque.repository;

import br.com.estoque.model.ModeloProduto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ModeloProdutoRepository {
    private final JdbcTemplate jdbc;
    public ModeloProdutoRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public ModeloProduto obterOuCriar(long loja,String nome,String marca,String categoria) {
        return jdbc.queryForObject("INSERT INTO models(tenant_id,name,brand,category) VALUES(?,?,?,?) ON CONFLICT(tenant_id,name,brand,category) DO UPDATE SET name=EXCLUDED.name RETURNING *",
            (r,n)->new ModeloProduto(r.getLong("id"),r.getLong("tenant_id"),r.getString("name"),r.getString("brand"),r.getString("category")),loja,nome,marca,categoria);
    }
}
