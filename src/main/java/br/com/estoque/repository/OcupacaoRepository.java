package br.com.estoque.repository;

import br.com.estoque.model.Ocupacao;
import br.com.estoque.dto.VinculoResumo;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OcupacaoRepository {
    private final JdbcTemplate jdbc;
    public OcupacaoRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public Ocupacao buscar(long loja,long produto,long posicao) {
        return jdbc.query("SELECT * FROM occupancy WHERE tenant_id=? AND product_id=? AND position_id=?",
            (r,n)->new Ocupacao(loja,produto,posicao,r.getBigDecimal("quantity")),loja,produto,posicao).stream().findFirst()
            .orElseThrow(()->new IllegalArgumentException("Vínculo não encontrado nesta loja."));
    }
    public Ocupacao bloquear(long loja,long produto,long posicao) {
        return jdbc.query("SELECT * FROM occupancy WHERE tenant_id=? AND product_id=? AND position_id=? FOR UPDATE",
            (r,n)->new Ocupacao(loja,produto,posicao,r.getBigDecimal("quantity")),loja,produto,posicao).stream().findFirst()
            .orElseThrow(()->new IllegalArgumentException("Vincule o produto à posição antes de movimentar."));
    }
    public void criar(long loja,long produto,long posicao) { jdbc.update("INSERT INTO occupancy(tenant_id,product_id,position_id) VALUES(?,?,?)",loja,produto,posicao); }
    public void atualizar(Ocupacao o) { jdbc.update("UPDATE occupancy SET quantity=? WHERE tenant_id=? AND product_id=? AND position_id=?",o.quantity(),o.tenantId(),o.productId(),o.positionId()); }
    public boolean removerVazio(long loja,long produto,long posicao) {
        return jdbc.update("DELETE FROM occupancy WHERE tenant_id=? AND product_id=? AND position_id=? AND quantity=0",loja,produto,posicao)>0;
    }
    public boolean temProduto(long loja,long produto) { return existe(loja,"product_id",produto,false); }
    public boolean temPosicao(long loja,long posicao) { return existe(loja,"position_id",posicao,false); }
    public boolean saldoProduto(long loja,long produto) { return existe(loja,"product_id",produto,true); }
    public boolean saldoPosicao(long loja,long posicao) { return existe(loja,"position_id",posicao,true); }
    private boolean existe(long loja,String coluna,long id,boolean comSaldo) {
        return Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM occupancy WHERE tenant_id=? AND "+coluna+"=?"+(comSaldo?" AND quantity>0":"")+")",Boolean.class,loja,id));
    }
    public void removerProdutoVazio(long loja,long id) { jdbc.update("DELETE FROM occupancy WHERE tenant_id=? AND product_id=? AND quantity=0",loja,id); }
    public void removerPosicaoVazia(long loja,long id) { jdbc.update("DELETE FROM occupancy WHERE tenant_id=? AND position_id=? AND quantity=0",loja,id); }
    public List<VinculoResumo> listar(long loja) {
        return jdbc.query("SELECT o.*,p.sku,p.barcode,p.size,p.color,m.name,s.code,s.sector,s.unit,s.device,s.pin FROM occupancy o JOIN products p ON p.id=o.product_id AND p.tenant_id=o.tenant_id JOIN models m ON m.id=p.model_id AND m.tenant_id=p.tenant_id JOIN positions s ON s.id=o.position_id AND s.tenant_id=o.tenant_id WHERE o.tenant_id=? AND p.active AND s.active ORDER BY s.code,p.sku",
            (r,n)->new VinculoResumo(new Ocupacao(loja,r.getLong("product_id"),r.getLong("position_id"),r.getBigDecimal("quantity")),r.getString("sku"),r.getString("barcode"),r.getString("name"),r.getString("size"),r.getString("color"),r.getString("code"),r.getString("sector"),r.getString("unit"),r.getString("device"),r.getObject("pin",Integer.class)),loja);
    }
}
