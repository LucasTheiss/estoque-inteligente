package br.com.estoque.service;

import br.com.estoque.dto.*;
import br.com.estoque.model.Posicao;
import br.com.estoque.repository.*;
import br.com.estoque.strategy.Quantidade;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PosicaoService {
    private final PosicaoRepository posicoes;
    private final OcupacaoRepository ocupacoes;
    private final AuditoriaRepository auditoria;
    private final Acesso acesso;
    public PosicaoService(PosicaoRepository posicoes,OcupacaoRepository ocupacoes,AuditoriaRepository auditoria,Acesso acesso) {
        this.posicoes=posicoes;this.ocupacoes=ocupacoes;this.auditoria=auditoria;this.acesso=acesso;
    }
    public List<PosicaoResumo> listar(long loja) { acesso.exigir(loja,"consultar");return posicoes.listar(loja); }
    public Posicao buscar(long loja,long id) { acesso.exigir(loja,"cadastros");return posicoes.buscar(loja,id); }
    @Transactional public void salvar(long loja,PosicaoForm f) {
        acesso.exigir(loja,"cadastros");String unidade=Campos.unidade(f.unit()),device=Campos.texto(f.device(),"dispositivo",false);
        if(!device.matches("[a-zA-Z0-9_-]*") || (f.pin()!=null && (f.pin()<0 || f.pin()>39)))throw new IllegalArgumentException("Dispositivo ou pino inválido.");
        if(unidade.equals("kg"))acesso.recurso(loja,"fractional");
        var capacidade=Quantidade.decimal(f.capacity(),unidade.equals("un")?0:3);
        if(capacidade.signum()<=0)throw new IllegalArgumentException("Capacidade deve ser positiva.");
        if(f.id()!=null) {
            var antigo=posicoes.bloquear(loja,f.id());
            if(!antigo.unit().equals(unidade) && ocupacoes.temPosicao(loja,f.id()))throw new IllegalArgumentException("Remova vínculos antes de alterar a unidade.");
        }
        posicoes.salvar(new Posicao(f.id(),loja,Campos.texto(f.code(),"código",true),Campos.texto(f.sector(),"setor",true),
            Campos.coordenada(f.rowNo()),Campos.coordenada(f.colNo()),capacidade,unidade,device,f.pin(),true));
        auditoria.registrar(loja,acesso.usuario().id(),"posicao.salvar",f.code());
    }
    @Transactional public void excluir(long loja,long id) {
        acesso.exigir(loja,"cadastros");posicoes.bloquear(loja,id);
        if(ocupacoes.saldoPosicao(loja,id))throw new IllegalArgumentException("Zere o estoque antes de excluir.");
        ocupacoes.removerPosicaoVazia(loja,id);posicoes.desativar(loja,id);
        auditoria.registrar(loja,acesso.usuario().id(),"posicao.excluir",String.valueOf(id));
    }
}
