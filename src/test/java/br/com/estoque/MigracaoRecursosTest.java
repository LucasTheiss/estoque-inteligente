package br.com.estoque;

import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "TEST_DATABASE_URL", matches = ".+")
class MigracaoRecursosTest {
    @Test
    void migracaoPreservaRecursosEDadosDasLojasExistentes() {
        String schema = "migracao_" + UUID.randomUUID().toString().replace("-", "");
        var fonte = new DriverManagerDataSource(System.getenv("TEST_DATABASE_URL"),
                System.getenv().getOrDefault("DATABASE_USER", "estoque"), System.getenv("DATABASE_PASSWORD"));
        var jdbc = new JdbcTemplate(fonte);
        try {
            Flyway.configure().dataSource(fonte).schemas(schema).defaultSchema(schema).target("2").load().migrate();
            jdbc.update("INSERT INTO " + schema + ".tenants(slug,name,enterprise,active) VALUES "
                    + "('basica','Loja básica',false,true),('completa','Loja completa',true,false)");
            var antes = jdbc.queryForList("SELECT id,slug,name,active FROM " + schema + ".tenants ORDER BY id");
            var flyway = Flyway.configure().dataSource(fonte).schemas(schema).defaultSchema(schema).load();
            assertEquals(1, flyway.migrate().migrationsExecuted);
            assertEquals(antes, jdbc.queryForList("SELECT id,slug,name,active FROM " + schema + ".tenants ORDER BY id"));
            assertEquals(0, jdbc.queryForObject("SELECT count(*) FROM " + schema
                    + ".tenants WHERE reports IS DISTINCT FROM (slug='completa') OR occupancy IS DISTINCT FROM reports", Integer.class));
            jdbc.update("UPDATE " + schema + ".tenants SET occupancy=false WHERE slug='completa'");
            assertEquals(0, flyway.migrate().migrationsExecuted);
            assertTrue(jdbc.queryForObject("SELECT reports AND NOT occupancy FROM " + schema
                    + ".tenants WHERE slug='completa'", Boolean.class));
            jdbc.update("INSERT INTO " + schema + ".tenants(slug,name) VALUES ('nova','Nova')");
            assertTrue(jdbc.queryForObject("SELECT NOT reports AND NOT occupancy FROM " + schema
                    + ".tenants WHERE slug='nova'", Boolean.class));
        } finally {
            jdbc.execute("DROP SCHEMA IF EXISTS " + schema + " CASCADE");
        }
    }
}
