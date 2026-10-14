package br.com.estoque.repository;

import br.com.estoque.model.TentativaLogin;
import java.time.OffsetDateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TentativaLoginRepository {
    private final JdbcTemplate jdbc;
    public TentativaLoginRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public TentativaLogin registrar(String chave) {
        jdbc.update("DELETE FROM login_attempts WHERE expires_at<now()");
        return jdbc.queryForObject("INSERT INTO login_attempts(key,attempts) VALUES(?,1) ON CONFLICT(key) DO UPDATE SET attempts=login_attempts.attempts+1 RETURNING *",
            (r,n)->new TentativaLogin(r.getString("key"),r.getInt("attempts"),r.getObject("expires_at",OffsetDateTime.class)),chave);
    }
    public void limpar(String chave) { jdbc.update("DELETE FROM login_attempts WHERE key=?",chave); }
}
