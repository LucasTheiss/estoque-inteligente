package br.com.estoque.repository;

import br.com.estoque.model.Posicao;
import br.com.estoque.dto.PosicaoResumo;
import java.sql.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PosicaoRepository {
    private final JdbcTemplate jdbc;
    public PosicaoRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    static Posicao ler(ResultSet r) throws SQLException {
        return new Posicao(r.getLong("id"),r.getLong("tenant_id"),r.getString("code"),r.getString("sector"),r.getInt("row_no"),r.getInt("col_no"),
            r.getBigDecimal("capacity"),r.getString("unit"),r.getString("device"),r.getObject("pin",Integer.class),r.getBoolean("active"));
    }
    public Posicao buscar(long loja,long id) { return obter(loja,id,""); }
    public Posicao bloquear(long loja,long id) { return obter(loja,id," FOR UPDATE"); }
    public Posicao compartilhar(long loja,long id) { return obter(loja,id," FOR SHARE"); }
    private Posicao obter(long loja,long id,String lock) {
        return jdbc.query("SELECT * FROM positions WHERE tenant_id=? AND id=? AND active"+lock,(r,n)->ler(r),loja,id).stream().findFirst()
            .orElseThrow(()->new IllegalArgumentException("Posição não encontrada."));
    }
    public List<PosicaoResumo> listar(long loja) {
        return jdbc.query("SELECT p.*,coalesce(sum(o.quantity),0) quantity FROM positions p LEFT JOIN occupancy o ON o.tenant_id=p.tenant_id AND o.position_id=p.id WHERE p.tenant_id=? AND p.active GROUP BY p.id ORDER BY p.sector,p.row_no,p.col_no,p.code",
            (r,n)->new PosicaoResumo(ler(r),r.getBigDecimal("quantity")),loja);
    }
    public long salvar(Posicao p) {
        if(p.id()==null) return jdbc.queryForObject("INSERT INTO positions(tenant_id,code,sector,row_no,col_no,capacity,unit,device,pin) VALUES(?,?,?,?,?,?,?,?,?) RETURNING id",
            Long.class,p.tenantId(),p.code(),p.sector(),p.rowNo(),p.colNo(),p.capacity(),p.unit(),p.device(),p.pin());
        jdbc.update("UPDATE positions SET code=?,sector=?,row_no=?,col_no=?,capacity=?,unit=?,device=?,pin=? WHERE tenant_id=? AND id=?",
            p.code(),p.sector(),p.rowNo(),p.colNo(),p.capacity(),p.unit(),p.device(),p.pin(),p.tenantId(),p.id());
        return p.id();
    }
    public void desativar(long loja,long id) { jdbc.update("UPDATE positions SET active=false WHERE tenant_id=? AND id=?",loja,id); }
}
