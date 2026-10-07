package br.com.estoque.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Singleton: provê conexão SQLite para os exemplos, sem configurar persistência completa. */
public final class ConexaoBanco {
    private static final ConexaoBanco INSTANCE = new ConexaoBanco();
    private final String url = "jdbc:sqlite:estoque-inteligente.db";

    private ConexaoBanco() {}

    public static ConexaoBanco getInstance() { return INSTANCE; }

    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
