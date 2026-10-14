package br.com.estoque.service;

import br.com.estoque.dto.VinculoResumo;
import br.com.estoque.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VinculoService {
    private final ProdutoRepository produtos;
    private final PosicaoRepository posicoes;
    private final OcupacaoRepository ocupacoes;
    private final AuditoriaRepository auditoria;
    private final Acesso acesso;
    public VinculoService(ProdutoRepository produtos,PosicaoRepository posicoes,OcupacaoRepository ocupacoes,AuditoriaRepository auditoria,Acesso acesso) {
        this.produtos=produtos;this.posicoes=posicoes;this.ocupacoes=ocupacoes;this.auditoria=auditoria;this.acesso=acesso;
    }
    public List<VinculoResumo> listar(long loja) { acesso.exigir(loja,"consultar");return ocupacoes.listar(loja); }
    @Transactional public void salvar(long loja,long produto,long posicao,boolean excluir) {
        acesso.exigir(loja,"cadastros");
        var p=produtos.compartilhar(loja,produto);var s=posicoes.compartilhar(loja,posicao);
        if(excluir) {
            if(!ocupacoes.removerVazio(loja,produto,posicao))throw new IllegalArgumentException("Zere o saldo antes de remover o vínculo.");
        } else {
            if(!p.unit().equals(s.unit()))throw new IllegalArgumentException("Produto e posição devem usar a mesma unidade.");
            ocupacoes.criar(loja,produto,posicao);
        }
        auditoria.registrar(loja,acesso.usuario().id(),"vinculo."+(excluir?"excluir":"criar"),produto+":"+posicao);
    }
}
