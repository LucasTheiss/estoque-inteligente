package br.com.estoque.repository;

import br.com.estoque.domain.Produto;
import java.util.Optional;

public interface ProdutoRepository {
    Optional<Produto> buscarPorSku(String sku);
    void salvar(Produto produto);
}
