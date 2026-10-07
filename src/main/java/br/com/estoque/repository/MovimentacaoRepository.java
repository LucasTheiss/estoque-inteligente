package br.com.estoque.repository;

import br.com.estoque.domain.Movimentacao;

public interface MovimentacaoRepository {
    void registrar(Movimentacao movimentacao);
}
