package br.com.estoque.databasefirst;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.hamcrest.Matchers.containsString;
import static br.com.estoque.databasefirst.generated.tables.Models.MODELS;
import static br.com.estoque.databasefirst.generated.tables.Products.PRODUCTS;
import static br.com.estoque.databasefirst.generated.tables.Tenants.TENANTS;
import br.com.estoque.databasefirst.generated.tables.daos.ModelsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.jooq.DSLContext;
import java.util.UUID;
import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("databasefirst")
@Transactional
class DatabaseFirstTest {
    @Autowired ModelService service;
    @Autowired MockMvc mvc;
    @Autowired DSLContext db;
    @Autowired JdbcTemplate jdbc;
    @Autowired Long demoTenant;

    @Test void crudRealComRecordPojoEDao() throws Exception {
        String name = "DF-" + UUID.randomUUID();
        mvc.perform(post("/models").with(user("professor").roles("DEMO")).with(csrf())
            .param("name", name).param("brand", "Marca").param("category", "Teste"))
            .andExpect(status().is3xxRedirection());
        Long id = jdbc.queryForObject("select id from models where name=? and tenant_id=?", Long.class, name, demoTenant);
        assertNotNull(id);
        assertEquals(name, new ModelsDao(db.configuration()).findById(id).getName());
        mvc.perform(get("/").with(user("professor").roles("DEMO")))
            .andExpect(status().isOk()).andExpect(content().string(containsString(name)));
        mvc.perform(get("/").param("id", id.toString()).with(user("professor").roles("DEMO")))
            .andExpect(status().isOk()).andExpect(content().string(containsString("Salvar alterações")));
        mvc.perform(post("/models").with(user("professor").roles("DEMO")).with(csrf())
            .param("id", id.toString()).param("name", name + " editado").param("brand", "Nova marca"))
            .andExpect(status().is3xxRedirection());
        assertEquals("Nova marca", jdbc.queryForObject("select brand from models where id=?", String.class, id));
        mvc.perform(post("/models/{id}/delete", id).with(user("professor").roles("DEMO")).with(csrf()))
            .andExpect(status().is3xxRedirection());
        assertEquals(0, jdbc.queryForObject("select count(*) from models where id=?", Integer.class, id));
        assertEquals(3, jdbc.queryForObject("select count(*) from audit where detail->>'modelId'=?", Integer.class, id.toString()));
    }
    @Test void exigeAutenticacaoECsrf() throws Exception {
        mvc.perform(get("/")).andExpect(status().is3xxRedirection());
        mvc.perform(post("/models").with(user("professor").roles("DEMO")).param("name", "Bloqueado"))
            .andExpect(status().isForbidden());
        mvc.perform(get("/").with(user("outro").roles("OUTRO"))).andExpect(status().isForbidden());
    }
    @Test void validaNomeELimite() {
        assertThrows(IllegalArgumentException.class, () -> service.save(null, " ", "", ""));
        assertThrows(IllegalArgumentException.class, () -> service.save(null, "x".repeat(201), "", ""));
    }
    @Test void preservaModelosVinculados() {
        long id = service.save(null, "Vinculado", "", "");
        db.insertInto(PRODUCTS).set(PRODUCTS.TENANT_ID, demoTenant).set(PRODUCTS.MODEL_ID, id)
            .set(PRODUCTS.SKU, UUID.randomUUID().toString()).set(PRODUCTS.UNIT, "un")
            .set(PRODUCTS.PRICE, BigDecimal.ONE).execute();
        assertThrows(IllegalArgumentException.class, () -> service.delete(id));
        assertNotNull(service.get(id));
    }
    @Test void isolaOutraLoja() {
        long other = db.insertInto(TENANTS).set(TENANTS.SLUG, "test-" + UUID.randomUUID())
            .set(TENANTS.NAME, "Outra loja de teste").returning(TENANTS.ID).fetchOne().getId();
        long id = db.insertInto(MODELS).set(MODELS.TENANT_ID, other).set(MODELS.NAME, "Outro")
            .returning(MODELS.ID).fetchOne().getId();
        assertThrows(IllegalArgumentException.class, () -> service.get(id));
        assertThrows(IllegalArgumentException.class, () -> service.save(id, "Alterado", "", ""));
        assertThrows(IllegalArgumentException.class, () -> service.delete(id));
        assertTrue(service.list().stream().noneMatch(m -> m.getId() == id));
    }
    @Test void rejeitaDuplicidade() {
        service.save(null, "Unico", "", "");
        assertThrows(RuntimeException.class, () -> service.save(null, "Unico", "", ""));
    }
}
