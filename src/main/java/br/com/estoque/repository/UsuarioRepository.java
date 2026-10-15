package br.com.estoque.repository;

import br.com.estoque.model.Usuario;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {
    private final JdbcTemplate jdbc;
    private static final RowMapper<Usuario> MAP = (r,n) -> new Usuario(r.getLong("id"),r.getObject("tenant_id",Long.class),
        r.getString("username"),r.getString("password_hash"),r.getString("role"),r.getInt("auth_version"),r.getBoolean("active"));
    public UsuarioRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public Optional<Usuario> autenticar(String loja,String nome) {
        return jdbc.query("SELECT u.* FROM users u LEFT JOIN tenants t ON t.id=u.tenant_id WHERE u.username=? AND u.active AND ((?='global' AND u.role='superadmin') OR (t.slug=? AND t.active))",
            MAP,nome,loja,loja).stream().findFirst();
    }
    public Optional<Usuario> porId(long id) { return jdbc.query("SELECT * FROM users WHERE id=?",MAP,id).stream().findFirst(); }
    public Usuario buscar(long loja,long id) {
        return jdbc.query("SELECT * FROM users WHERE tenant_id=? AND id=? AND active",MAP,loja,id).stream().findFirst()
            .orElseThrow(()->new IllegalArgumentException("Usuário não encontrado."));
    }
    public List<Usuario> listar(long loja) { return jdbc.query("SELECT * FROM users WHERE tenant_id=? AND active ORDER BY username",MAP,loja); }
    public long criar(long loja,String nome,String hash,String perfil) {
        return jdbc.queryForObject("INSERT INTO users(tenant_id,username,password_hash,role) VALUES(?,?,?,?) RETURNING id",Long.class,loja,nome,hash,perfil);
    }
    public void criarGlobal(String hash) {
        jdbc.update("INSERT INTO users(username,password_hash,role) VALUES('superadmin',?,'superadmin') ON CONFLICT(username) WHERE tenant_id IS NULL DO NOTHING",hash);
    }
    public long administradores(long loja) { return jdbc.queryForObject("SELECT count(*) FROM users WHERE tenant_id=? AND active AND role='admin'",Long.class,loja); }
    public void atualizar(long loja,long id,String nome,String perfil,String hash) {
        jdbc.update("UPDATE users SET username=?,role=?,password_hash=?,auth_version=auth_version+1 WHERE tenant_id=? AND id=?",nome,perfil,hash,loja,id);
    }
    public void desativar(long loja,long id) { jdbc.update("UPDATE users SET active=false,auth_version=auth_version+1 WHERE tenant_id=? AND id=?",loja,id); }
}
