package br.com.estoque.service;

import br.com.estoque.dto.*;
import br.com.estoque.repository.*;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class HistoricoService {
    private final MovimentacaoRepository movimentos;
    private final AuditoriaRepository auditoria;
    private final Acesso acesso;

    public HistoricoService(
            MovimentacaoRepository movimentos, AuditoriaRepository auditoria, Acesso acesso) {
        this.movimentos = movimentos;
        this.auditoria = auditoria;
        this.acesso = acesso;
    }

    public List<MovimentacaoResumo> consultar(long loja, String tipo, String inicio, String fim) {
        acesso.exigir(loja, "historico");
        return filtrar(loja, tipo, inicio, fim);
    }

    public List<MovimentacaoResumo> estornaveis(long loja) {
        acesso.exigir(loja, "estorno");
        return filtrar(loja, "", "", "").stream()
                .filter(
                        m ->
                                !m.estornado()
                                        && Set.of("entrada", "saida")
                                                .contains(m.movimento().kind()))
                .toList();
    }

    public List<AuditoriaResumo> auditoria(long loja) {
        acesso.exigir(loja, "historico");
        return auditoria.recentes(loja);
    }

    List<MovimentacaoResumo> filtrar(long loja, String tipo, String inicio, String fim) {
        if (!tipo.isBlank() && !Set.of("entrada", "saida", "ajuste", "estorno").contains(tipo))
            throw new IllegalArgumentException("Tipo de movimento inválido.");
        LocalDate de = inicio.isBlank() ? null : LocalDate.parse(inicio),
                ate = fim.isBlank() ? null : LocalDate.parse(fim);
        if (de != null && ate != null && de.isAfter(ate))
            throw new IllegalArgumentException("A data inicial deve preceder a data final.");
        return movimentos.historico(loja, tipo, de, ate);
    }
}
