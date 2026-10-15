package br.com.estoque.repository;

import br.com.estoque.model.*;
import br.com.estoque.dto.ProdutoResumo;
import java.sql.*;
import java.text.Normalizer;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProdutoRepository {
    private final JdbcTemplate jdbc;
    public ProdutoRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    static Produto ler(ResultSet r) throws SQLException {
        return new Produto(r.getLong("id"),r.getLong("tenant_id"),r.getLong("model_id"),r.getString("sku"),r.getString("barcode"),
            r.getString("color"),r.getString("size"),r.getString("unit"),r.getBigDecimal("price"),r.getString("attributes"),r.getBoolean("active"));
    }
    public Produto buscar(long loja,long id) { return obter(loja,id,""); }
    public Produto bloquear(long loja,long id) { return obter(loja,id," FOR UPDATE"); }
    public Produto compartilhar(long loja,long id) { return obter(loja,id," FOR SHARE"); }
    private Produto obter(long loja,long id,String lock) {
        return jdbc.query("SELECT * FROM products WHERE tenant_id=? AND id=? AND active"+lock,(r,n)->ler(r),loja,id).stream().findFirst()
            .orElseThrow(()->new IllegalArgumentException("Produto não encontrado."));
    }
    public List<ProdutoResumo> pesquisar(long loja,String busca,int pagina) {
        String sql="""
            SELECT p.*,m.name,m.brand,m.category,
              coalesce((SELECT sum(o.quantity) FROM occupancy o WHERE o.tenant_id=p.tenant_id AND o.product_id=p.id),0) quantity
            FROM products p JOIN models m ON m.tenant_id=p.tenant_id AND m.id=p.model_id
            WHERE p.tenant_id=? AND p.active
            """;
        List<Object> args=new ArrayList<>(List.of(loja));
        String normalizada=Normalizer.normalize(busca,Normalizer.Form.NFD)
            .replaceAll("\\p{M}+","").toLowerCase(Locale.ROOT)
            .replaceAll("[.,;:!?…]+(?=\\s|$)"," ").strip();
        for(String termo:normalizada.split("\\s+")) {
            if(termo.isBlank()) continue;
            sql+=" AND translate(lower(concat_ws(' ',p.sku,p.barcode,p.color,p.size,p.unit,p.price,p.attributes::text,m.name,m.brand,m.category,(SELECT string_agg(s.code || ' ' || s.sector || ' ' || o.quantity::text,' ') FROM occupancy o JOIN positions s ON s.id=o.position_id AND s.tenant_id=o.tenant_id WHERE o.tenant_id=p.tenant_id AND o.product_id=p.id))), 'áàâãäåéèêëíìîïóòôõöúùûüçñýÿ', 'aaaaaaeeeeiiiiooooouuuucnyy') LIKE ?";
            args.add("%"+termo.replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%");
        }
        args.add(Math.multiplyExact(pagina,100));
        return jdbc.query(sql+" ORDER BY m.name,p.sku LIMIT 101 OFFSET ?",(r,n)->new ProdutoResumo(ler(r),
            new ModeloProduto(r.getLong("model_id"),loja,r.getString("name"),r.getString("brand"),r.getString("category")),r.getBigDecimal("quantity")),args.toArray());
    }
    public ProdutoResumo detalhe(long loja,long id) {
        return jdbc.query("SELECT p.*,m.name,m.brand,m.category,coalesce((SELECT sum(quantity) FROM occupancy WHERE tenant_id=p.tenant_id AND product_id=p.id),0) quantity FROM products p JOIN models m ON m.tenant_id=p.tenant_id AND m.id=p.model_id WHERE p.tenant_id=? AND p.id=? AND p.active",
            (r,n)->new ProdutoResumo(ler(r),new ModeloProduto(r.getLong("model_id"),loja,r.getString("name"),r.getString("brand"),r.getString("category")),r.getBigDecimal("quantity")),loja,id)
            .stream().findFirst().orElseThrow(()->new IllegalArgumentException("Produto não encontrado."));
    }
    public long salvar(Produto p) {
        if(p.id()==null) return jdbc.queryForObject("INSERT INTO products(tenant_id,model_id,sku,barcode,color,size,unit,price,attributes) VALUES(?,?,?,?,?,?,?,?,?::jsonb) RETURNING id",
            Long.class,p.tenantId(),p.modelId(),p.sku(),p.barcode(),p.color(),p.size(),p.unit(),p.price(),p.attributes());
        jdbc.update("UPDATE products SET model_id=?,sku=?,barcode=?,color=?,size=?,unit=?,price=?,attributes=?::jsonb WHERE tenant_id=? AND id=?",
            p.modelId(),p.sku(),p.barcode(),p.color(),p.size(),p.unit(),p.price(),p.attributes(),p.tenantId(),p.id());
        return p.id();
    }
    public boolean temFracionados(long loja) { return Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM products WHERE tenant_id=? AND active AND unit='kg')",Boolean.class,loja)); }
    public void desativar(long loja,long id) { jdbc.update("UPDATE products SET active=false WHERE tenant_id=? AND id=?",loja,id); }
}
