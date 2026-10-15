package br.com.estoque.repository;

import br.com.estoque.model.PermissaoPerfil;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PermissaoRepository {
    private final JdbcTemplate jdbc;

    public PermissaoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Set<String> doPerfil(long loja, String perfil) {
        return new HashSet<>(
                jdbc.queryForList(
                        "SELECT permission FROM role_permissions WHERE tenant_id=? AND role=?",
                        String.class,
                        loja,
                        perfil));
    }

    public List<PermissaoPerfil> listar(long loja) {
        return jdbc.query(
                "SELECT * FROM role_permissions WHERE tenant_id=? ORDER BY role,permission",
                (r, n) -> new PermissaoPerfil(loja, r.getString("role"), r.getString("permission")),
                loja);
    }

    public void conceder(long loja, String perfil, String permissao) {
        jdbc.update(
                "INSERT INTO role_permissions VALUES(?,?,?) ON CONFLICT DO NOTHING",
                loja,
                perfil,
                permissao);
    }

    public void limparPerfil(long loja, String perfil) {
        jdbc.update("DELETE FROM role_permissions WHERE tenant_id=? AND role=?", loja, perfil);
    }
}
