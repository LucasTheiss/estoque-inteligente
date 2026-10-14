package br.com.estoque.repository;

import br.com.estoque.model.RegistroAuditoria;
import br.com.estoque.dto.AuditoriaResumo;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditoriaRepository {
    private final JdbcTemplate jdbc;
    public AuditoriaRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public void registrar(Long loja,long ator,String acao,String detalhe) {
        jdbc.update("INSERT INTO audit(tenant_id,actor_id,action,detail) VALUES(?,?,?,jsonb_build_object('descricao',?::text))",loja,ator,acao,detalhe);
    }
    public List<AuditoriaResumo> recentes(long loja) {
        return jdbc.query("SELECT a.*,u.username FROM audit a LEFT JOIN users u ON u.id=a.actor_id WHERE a.tenant_id=? ORDER BY a.id DESC LIMIT 100",
            (r,n)->new AuditoriaResumo(new RegistroAuditoria(r.getLong("id"),r.getObject("tenant_id",Long.class),r.getObject("actor_id",Long.class),
                r.getString("action"),r.getString("detail"),r.getObject("created_at",OffsetDateTime.class)),r.getString("username")),loja);
    }
}
