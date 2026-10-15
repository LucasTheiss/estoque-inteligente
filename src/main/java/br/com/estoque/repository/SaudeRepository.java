package br.com.estoque.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SaudeRepository {
    private final JdbcTemplate jdbc;

    public SaudeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean disponivel() {
        return Integer.valueOf(1).equals(jdbc.queryForObject("SELECT 1", Integer.class));
    }
}
