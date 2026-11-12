package br.com.estoque.databasefirst;

import static br.com.estoque.databasefirst.generated.tables.Models.MODELS;
import static br.com.estoque.databasefirst.generated.tables.Products.PRODUCTS;
import static br.com.estoque.databasefirst.generated.tables.Audit.AUDIT;
import br.com.estoque.databasefirst.generated.tables.pojos.Models;
import br.com.estoque.databasefirst.generated.tables.records.ModelsRecord;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModelService {
    private final DSLContext db;
    private final long tenant;
    public ModelService(DSLContext db, Long demoTenant) { this.db = db; this.tenant = demoTenant; }
    public List<Models> list() {
        return db.selectFrom(MODELS).where(MODELS.TENANT_ID.eq(tenant)).orderBy(MODELS.ID).fetchInto(Models.class);
    }
    public Models get(long id) { return record(id, false).into(Models.class); }
    private ModelsRecord record(long id, boolean lock) {
        var query = db.selectFrom(MODELS).where(MODELS.ID.eq(id).and(MODELS.TENANT_ID.eq(tenant)));
        ModelsRecord result = lock ? query.forUpdate().fetchOne() : query.fetchOne();
        if (result == null) throw new IllegalArgumentException("Modelo de demonstracao nao encontrado.");
        return result;
    }
    @Transactional
    public long save(Long id, String name, String brand, String category) {
        name = text(name, true); brand = text(brand, false); category = text(category, false);
        ModelsRecord model = id == null ? db.newRecord(MODELS) : record(id, true);
        model.setTenantId(tenant);
        model.setName(name); model.setBrand(brand); model.setCategory(category);
        model.store();
        audit("databasefirst.salvar", model.getId());
        return model.getId();
    }
    @Transactional
    public void delete(long id) {
        var model = record(id, true);
        if (db.fetchExists(PRODUCTS, PRODUCTS.TENANT_ID.eq(tenant).and(PRODUCTS.MODEL_ID.eq(id))))
            throw new IllegalArgumentException("Modelo vinculado a produtos: preserve o historico.");
        model.delete();
        audit("databasefirst.excluir", id);
    }
    private void audit(String action, long id) {
        db.insertInto(AUDIT).set(AUDIT.TENANT_ID, tenant).set(AUDIT.ACTION, action)
            .set(AUDIT.DETAIL, JSONB.valueOf("{\"modelId\":" + id + "}")).execute();
    }
    private String text(String value, boolean required) {
        String clean = value == null ? "" : value.strip();
        if ((required && clean.isEmpty()) || clean.length() > 200)
            throw new IllegalArgumentException("Informe nome e use no maximo 200 caracteres por campo.");
        return clean;
    }
}
