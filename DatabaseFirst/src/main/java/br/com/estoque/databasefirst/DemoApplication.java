package br.com.estoque.databasefirst;

import java.util.Arrays;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.jooq.DSLContext;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) { SpringApplication.run(DemoApplication.class, args); }

    @Bean
    Long demoTenant(DSLContext db, Environment env) {
        if (!Arrays.asList(env.getActiveProfiles()).contains("databasefirst"))
            throw new IllegalStateException("Ative exclusivamente o ambiente de demonstracao databasefirst.");
        if (!"estoque_databasefirst".equals(db.fetchValue("select current_database()", String.class)))
            throw new IllegalStateException("Banco exclusivo estoque_databasefirst obrigatorio.");
        var t = br.com.estoque.databasefirst.generated.tables.Tenants.TENANTS;
        Long id = db.select(t.ID).from(t).where(t.SLUG.eq("databasefirst")).fetchOne(t.ID);
        if (id == null) throw new IllegalStateException("Execute demo.ps1 prepare primeiro.");
        return id;
    }
}
