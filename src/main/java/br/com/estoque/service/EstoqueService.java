package br.com.estoque.service;

import br.com.estoque.dto.MovimentoForm;
import br.com.estoque.model.*;
import br.com.estoque.repository.*;
import br.com.estoque.strategy.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstoqueService {
    private final ProdutoRepository produtos;
    private final PosicaoRepository posicoes;
    private final OcupacaoRepository ocupacoes;
    private final MovimentacaoRepository movimentos;
    private final AuditoriaRepository auditoria;
    private final Acesso acesso;
    private static final Map<String, CalculoEstoque> OPERACOES =
            Map.of(
                    "entrada",
                    new EntradaEstoque(),
                    "saida",
                    new SaidaEstoque(),
                    "ajuste",
                    new AjusteEstoque());

    public EstoqueService(
            ProdutoRepository produtos,
            PosicaoRepository posicoes,
            OcupacaoRepository ocupacoes,
            MovimentacaoRepository movimentos,
            AuditoriaRepository auditoria,
            Acesso acesso) {
        this.produtos = produtos;
        this.posicoes = posicoes;
        this.ocupacoes = ocupacoes;
        this.movimentos = movimentos;
        this.auditoria = auditoria;
        this.acesso = acesso;
    }

    @Transactional
    public Movimentacao movimentar(long loja, MovimentoForm f) {
        if (f.kind() == null || !OPERACOES.containsKey(f.kind()))
            throw new IllegalArgumentException("Operação inválida.");
        return executar(
                loja,
                f.productId(),
                f.positionId(),
                f.kind(),
                f.quantity(),
                f.reason(),
                f.requestKey(),
                null);
    }

    @Transactional
    public Movimentacao estornar(long loja, long id, String motivo, String chave) {
        acesso.exigir(loja, "estorno");
        var original = movimentos.buscar(loja, id);
        return executar(
                loja,
                original.productId(),
                original.positionId(),
                "estorno",
                original.delta().abs().toPlainString(),
                motivo,
                chave,
                id);
    }

    private Movimentacao executar(
            long loja,
            long produto,
            long posicao,
            String tipo,
            String quantidade,
            String motivo,
            String chave,
            Long original) {
        acesso.exigir(loja, tipo);
        if (chave == null
                || chave.isBlank()
                || chave.length() > 100
                || motivo == null
                || motivo.isBlank()
                || motivo.length() > 1000)
            throw new IllegalArgumentException("Informe a justificativa e uma chave válida.");
        var ator = acesso.usuario();
        String fingerprint =
                UUID.nameUUIDFromBytes(
                                Arrays.asList(
                                                produto,
                                                posicao,
                                                tipo,
                                                quantidade,
                                                motivo,
                                                ator.id(),
                                                original)
                                        .toString()
                                        .getBytes(StandardCharsets.UTF_8))
                        .toString();
        movimentos.bloquearChave(loja, chave);
        var repetida = movimentos.porChave(loja, chave);
        if (repetida.isPresent()) {
            if (!repetida.get().fingerprint().equals(fingerprint))
                throw new IllegalArgumentException("Chave já usada para outra operação.");
            return repetida.get();
        }
        var p = produtos.compartilhar(loja, produto);
        var local = posicoes.compartilhar(loja, posicao);
        if (!p.unit().equals(local.unit()))
            throw new IllegalArgumentException("Unidades incompatíveis.");
        Quantidade politica =
                p.unit().equals("un") ? new QuantidadeInteira() : new QuantidadeFracionada();
        if (p.unit().equals("kg")) acesso.recurso(loja, "fractional");
        BigDecimal valor = politica.validar(quantidade);
        BigDecimal antes = ocupacoes.bloquear(loja, produto, posicao).quantity();
        BigDecimal depois;
        if (original != null) {
            var mov = movimentos.bloquear(loja, original);
            if (!List.of("entrada", "saida").contains(mov.kind())
                    || mov.productId() != produto
                    || mov.positionId() != posicao)
                throw new IllegalArgumentException("Estorno inválido.");
            if (movimentos.estornada(loja, original))
                throw new IllegalArgumentException("Movimentação já estornada.");
            depois = antes.subtract(mov.delta());
            if (depois.signum() < 0)
                throw new IllegalArgumentException("Saldo insuficiente para estornar a entrada.");
        } else depois = OPERACOES.get(tipo).executar(antes, valor);
        ocupacoes.atualizar(new Ocupacao(loja, produto, posicao, depois));
        var resultado =
                movimentos.registrar(
                        new Movimentacao(
                                null,
                                loja,
                                produto,
                                posicao,
                                ator.id(),
                                tipo,
                                depois.subtract(antes),
                                antes,
                                depois,
                                motivo,
                                null,
                                chave,
                                fingerprint,
                                original));
        auditoria.registrar(
                loja,
                ator.id(),
                tipo,
                "Movimento " + resultado.id() + "; " + antes + " → " + depois + "; " + motivo);
        return resultado;
    }
}
