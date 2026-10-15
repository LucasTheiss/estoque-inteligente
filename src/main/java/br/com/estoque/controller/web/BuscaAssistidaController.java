package br.com.estoque.controller.web;

import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BuscaAssistidaController {
    private final ProdutoService produtos;
    private final VinculoService vinculos;
    private final Acesso acesso;

    public BuscaAssistidaController(
            ProdutoService produtos, VinculoService vinculos, Acesso acesso) {
        this.produtos = produtos;
        this.vinculos = vinculos;
        this.acesso = acesso;
    }

    @GetMapping("/busca-assistida")
    public String buscar(@RequestParam(defaultValue = "") String q, Model m, HttpSession s) {
        long loja = acesso.loja(s);
        acesso.recurso(loja, "iot");
        m.addAttribute("title", "Busca por voz");
        m.addAttribute("q", q);
        m.addAttribute("produtos", produtos.pesquisar(loja, q, 0).stream().limit(100).toList());
        m.addAttribute("vinculos", vinculos.listar(loja));
        return "voz";
    }
}
