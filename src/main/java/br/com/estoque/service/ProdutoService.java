package br.com.estoque.service;

import br.com.estoque.dto.*;
import br.com.estoque.model.Produto;
import br.com.estoque.repository.*;
import br.com.estoque.strategy.Quantidade;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {
    private final ProdutoRepository produtos;
    private final ModeloProdutoRepository modelos;
    private final OcupacaoRepository ocupacoes;
    private final AuditoriaRepository auditoria;
    private final Acesso acesso;
    private final ObjectMapper json;
    private final VinculoService vinculos;
    private final EstoqueService estoque;

    public ProdutoService(
            ProdutoRepository produtos,
            ModeloProdutoRepository modelos,
            OcupacaoRepository ocupacoes,
            AuditoriaRepository auditoria,
            Acesso acesso,
            ObjectMapper json,
            VinculoService vinculos,
            EstoqueService estoque) {
        this.produtos = produtos;
        this.modelos = modelos;
        this.ocupacoes = ocupacoes;
        this.auditoria = auditoria;
        this.acesso = acesso;
        this.json = json;
        this.vinculos = vinculos;
        this.estoque = estoque;
    }

    public List<ProdutoResumo> pesquisar(long loja, String busca, int pagina) {
        acesso.exigir(loja, "consultar");
        if (busca == null || busca.length() > 200 || pagina < 0 || pagina > 100000)
            throw new IllegalArgumentException("Busca ou página inválida.");
        return produtos.pesquisar(loja, busca, pagina);
    }

    public ProdutoResumo buscar(long loja, long id) {
        acesso.exigir(loja, "consultar");
        return produtos.detalhe(loja, id);
    }

    @Transactional
    public void salvar(long loja, ProdutoForm f) {
        acesso.exigir(loja, "cadastros");
        String unidade = Campos.unidade(f.unit());
        if (unidade.equals("kg")) acesso.recurso(loja, "fractional");
        if (f.id() != null) {
            var antigo = produtos.bloquear(loja, f.id());
            if (!antigo.unit().equals(unidade) && ocupacoes.temProduto(loja, f.id()))
                throw new IllegalArgumentException(
                        "Remova os vínculos antes de alterar a unidade.");
        }
        var modelo =
                modelos.obterOuCriar(
                        loja,
                        Campos.texto(f.name(), "nome", true),
                        Campos.texto(f.brand(), "marca", false),
                        Campos.texto(f.category(), "categoria", false));
        String sku = Campos.texto(f.sku(), "SKU", true);
        boolean grade = "grade".equals(f.structure()) && f.id() == null;
        List<String> tamanhos =
                grade
                        ? Arrays.stream(Campos.texto(f.sizes(), "tamanhos", true).split(",", -1))
                                .map(String::strip)
                                .toList()
                        : List.of(Campos.texto(f.size(), "tamanho", false));
        if (tamanhos.size() > 30
                || new HashSet<>(tamanhos).size() != tamanhos.size()
                || (grade && tamanhos.stream().anyMatch(t -> !t.matches("[\\p{L}0-9.-]{1,20}"))))
            throw new IllegalArgumentException(
                    "Grade inválida. Informe tamanhos distintos, separados por vírgula.");
        var saldos = saldosIniciais(f, grade, tamanhos, unidade);
        if (saldos.values().stream().anyMatch(q -> q.signum() > 0)) acesso.exigir(loja, "entrada");
        String atributos =
                f.attributes() == null || f.attributes().isBlank() ? "{}" : f.attributes();
        try {
            if (atributos.length() > 2000 || !json.readTree(atributos).isObject())
                throw new IllegalArgumentException();
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Características devem ser um objeto JSON com até 2.000 caracteres.");
        }
        Quantidade politicaPreco = new br.com.estoque.strategy.QuantidadeMonetaria();
        var preco = politicaPreco.validar(f.price());
        for (String tamanho : tamanhos) {
            long id =
                    produtos.salvar(
                            new Produto(
                                    f.id(),
                                    loja,
                                    modelo.id(),
                                    grade ? sku + "-" + tamanho : sku,
                                    tamanhos.size() > 1
                                            ? ""
                                            : Campos.texto(f.barcode(), "código de barras", false),
                                    Campos.texto(f.color(), "cor", false),
                                    tamanho,
                                    unidade,
                                    preco,
                                    atributos,
                                    true));
            if (!saldos.isEmpty()) {
                vinculos.salvar(loja, id, f.initialPositionId(), false);
                var quantidade = saldos.get(tamanho);
                if (quantidade.signum() > 0)
                    estoque.movimentar(
                            loja,
                            new MovimentoForm(
                                    id,
                                    f.initialPositionId(),
                                    "entrada",
                                    quantidade.toPlainString(),
                                    "Saldo inicial da grade",
                                    UUID.randomUUID().toString()));
            }
        }
        auditoria.registrar(loja, acesso.usuario().id(), "produto.salvar", sku);
    }

    private Map<String, BigDecimal> saldosIniciais(
            ProdutoForm f, boolean grade, List<String> tamanhos, String unidade) {
        String texto = f.initialQuantities();
        if (texto == null || texto.isBlank()) {
            if (f.initialPositionId() != null)
                throw new IllegalArgumentException(
                        "Informe o saldo de cada tamanho ou deixe a posição inicial vazia.");
            return Map.of();
        }
        if (!grade
                || f.initialPositionId() == null
                || f.initialPositionId() <= 0
                || texto.length() > 2000)
            throw new IllegalArgumentException(
                    "Saldo inicial exige uma nova grade e uma posição de destino.");
        Map<String, BigDecimal> saldos = new LinkedHashMap<>();
        for (String linha : texto.strip().split("\\R")) {
            String[] partes = linha.split("=", -1);
            if (partes.length != 2)
                throw new IllegalArgumentException("Informe um tamanho=quantidade por linha.");
            String tamanho = partes[0].strip();
            var quantidade =
                    Quantidade.decimal(
                            partes[1].strip().replace(',', '.'), unidade.equals("un") ? 0 : 3);
            if (!tamanhos.contains(tamanho) || saldos.putIfAbsent(tamanho, quantidade) != null)
                throw new IllegalArgumentException(
                        "Use cada tamanho da grade uma única vez no saldo inicial.");
        }
        if (saldos.size() != tamanhos.size())
            throw new IllegalArgumentException(
                    "Informe o saldo de todos os tamanhos; use zero onde não houver estoque.");
        return saldos;
    }

    @Transactional
    public void excluir(long loja, long id) {
        acesso.exigir(loja, "cadastros");
        produtos.bloquear(loja, id);
        if (ocupacoes.saldoProduto(loja, id))
            throw new IllegalArgumentException("Zere o estoque antes de excluir.");
        ocupacoes.removerProdutoVazio(loja, id);
        produtos.desativar(loja, id);
        auditoria.registrar(loja, acesso.usuario().id(), "produto.excluir", String.valueOf(id));
    }
}
