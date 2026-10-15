package br.com.estoque.service;

import br.com.estoque.model.*;
import br.com.estoque.repository.*;
import java.math.BigDecimal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemonstracaoService {
    private final LojaRepository lojas;
    private final UsuarioRepository usuarios;
    private final ModeloProdutoRepository modelos;
    private final ProdutoRepository produtos;
    private final PosicaoRepository posicoes;
    private final OcupacaoRepository ocupacoes;
    private final MovimentacaoRepository movimentos;
    private final AuditoriaRepository auditoria;
    private final PermissaoService permissoes;
    private final PasswordEncoder encoder;

    public DemonstracaoService(
            LojaRepository lojas,
            UsuarioRepository usuarios,
            ModeloProdutoRepository modelos,
            ProdutoRepository produtos,
            PosicaoRepository posicoes,
            OcupacaoRepository ocupacoes,
            MovimentacaoRepository movimentos,
            AuditoriaRepository auditoria,
            PermissaoService permissoes,
            PasswordEncoder encoder) {
        this.lojas = lojas;
        this.usuarios = usuarios;
        this.modelos = modelos;
        this.produtos = produtos;
        this.posicoes = posicoes;
        this.ocupacoes = ocupacoes;
        this.movimentos = movimentos;
        this.auditoria = auditoria;
        this.permissoes = permissoes;
        this.encoder = encoder;
    }

    @Transactional
    public void provisionar(boolean demo, String senhaDemo, String senhaGlobal) {
        lojas.bloquearProvisionamento();
        if (!senhaGlobal.isBlank()) {
            Campos.senha(senhaGlobal);
            usuarios.criarGlobal(encoder.encode(senhaGlobal));
        }
        if (!demo) return;
        Campos.senha(senhaDemo);
        seed("calcados", "Calçados Aurora", true, true, false, "un", senhaDemo);
        seed("granel", "Mercado Raiz", true, true, true, "kg", senhaDemo);
        seed("essencial", "Almoxarifado Base", false, false, false, "un", senhaDemo);
    }

    private void seed(
            String slug,
            String nome,
            boolean iot,
            boolean enterprise,
            boolean fractional,
            String unit,
            String senha) {
        if (lojas.porSlug(slug).isPresent())
            return; // Reiniciar preserva dados e senhas existentes.
        long loja = lojas.criar(slug, nome, iot, enterprise, fractional);
        permissoes.iniciais(loja);
        long ator = 0;
        for (String perfil : Acesso.PERFIS) {
            long id = usuarios.criar(loja, perfil, encoder.encode(senha), perfil);
            if (perfil.equals("admin")) ator = id;
        }
        String[] nomes =
                unit.equals("kg")
                        ? new String[] {
                            "Café especial", "Castanha de caju", "Arroz integral", "Granola natural"
                        }
                        : slug.equals("essencial")
                                ? new String[] {
                                    "Caixa organizadora",
                                    "Papel A4",
                                    "Fita adesiva",
                                    "Envelope kraft"
                                }
                                : new String[] {
                                    "Tênis Horizon",
                                    "Tênis Essential",
                                    "Bota Urban",
                                    "Sandália Serena"
                                };
        for (int i = 0; i < 8; i++) {
            long pos =
                    posicoes.salvar(
                            new Posicao(
                                    null,
                                    loja,
                                    (i < 4 ? "A" : "B") + (i % 4 + 1),
                                    i < 4 ? "Setor A" : "Setor B",
                                    i / 4,
                                    i % 4,
                                    new BigDecimal("30"),
                                    unit,
                                    iot ? "esp32-demo" : "",
                                    iot ? 16 + i : null,
                                    true));
            if (i >= 4) continue;
            var modelo =
                    modelos.obterOuCriar(
                            loja,
                            nomes[i],
                            unit.equals("kg") ? "Origem" : "Aurora",
                            unit.equals("kg")
                                    ? "Granel"
                                    : slug.equals("essencial") ? "Materiais" : "Calçados");
            long prod =
                    produtos.salvar(
                            new Produto(
                                    null,
                                    loja,
                                    modelo.id(),
                                    slug.toUpperCase() + "-" + (i + 1),
                                    "78900000000" + i,
                                    unit.equals("kg") ? "Natural" : i % 2 == 0 ? "Areia" : "Preto",
                                    slug.equals("calcados") ? String.valueOf(38 + i) : "",
                                    unit,
                                    new BigDecimal(
                                            unit.equals("kg")
                                                    ? "29.90"
                                                    : slug.equals("essencial")
                                                            ? "12.90"
                                                            : "249.90"),
                                    "{}",
                                    true));
            var qtd = BigDecimal.valueOf(6 + i * 3);
            ocupacoes.criar(loja, prod, pos);
            ocupacoes.atualizar(new Ocupacao(loja, prod, pos, qtd));
            movimentos.registrar(
                    new Movimentacao(
                            null,
                            loja,
                            prod,
                            pos,
                            ator,
                            "entrada",
                            qtd,
                            BigDecimal.ZERO,
                            qtd,
                            "Carga de demonstração",
                            null,
                            "seed-" + prod,
                            "seed",
                            null));
        }
        auditoria.registrar(
                loja, ator, "demo.criar", "Dados demonstrativos carregados automaticamente");
    }
}
