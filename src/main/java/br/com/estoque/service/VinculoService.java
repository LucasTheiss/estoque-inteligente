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
    public br.com.estoque.model.Ocupacao buscar(long loja,long produto,long posicao) {
        acesso.exigir(loja,"cadastros");return ocupacoes.buscar(loja,produto,posicao);
    }
    @Transactional public void editar(long loja,long produtoOriginal,long posicaoOriginal,long produto,long posicao) {
        acesso.exigir(loja,"cadastros");
        var p=produtos.compartilhar(loja,produto);var s=posicoes.compartilhar(loja,posicao);
        var antigo=ocupacoes.bloquear(loja,produtoOriginal,posicaoOriginal);
        if(antigo.quantity().signum()!=0)throw new IllegalArgumentException("Zere o saldo antes de editar o vínculo.");
        if(!p.unit().equals(s.unit()))throw new IllegalArgumentException("Produto e posição devem usar a mesma unidade.");
        if(produtoOriginal==produto && posicaoOriginal==posicao)throw new IllegalArgumentException("Escolha outro produto ou posição.");
        ocupacoes.criar(loja,produto,posicao);
        if(!ocupacoes.removerVazio(loja,produtoOriginal,posicaoOriginal))throw new IllegalArgumentException("Vínculo original indisponível.");
        auditoria.registrar(loja,acesso.usuario().id(),"vinculo.editar",produtoOriginal+":"+posicaoOriginal+" -> "+produto+":"+posicao);
    }
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
