package br.com.estoque.controller.web;

import br.com.estoque.dto.ProdutoForm;
import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProdutoController {
    private final ProdutoService produtos;
    private final Acesso acesso;
    private final PosicaoService posicoes;

    public ProdutoController(ProdutoService produtos, Acesso acesso, PosicaoService posicoes) {
        this.produtos = produtos;
        this.acesso = acesso;
        this.posicoes = posicoes;
    }

    @GetMapping("/produtos")
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long id,
            Model m,
            HttpSession s) {
        long loja = acesso.loja(s);
        acesso.exigir(loja, "cadastros");
        var lista = produtos.pesquisar(loja, q, page);
        m.addAttribute("title", "Produtos");
        m.addAttribute("q", q);
        m.addAttribute("page", page);
        m.addAttribute("hasNext", lista.size() > 100);
        m.addAttribute("posicoes", id == null ? posicoes.listar(loja) : java.util.List.of());
        m.addAttribute("produtos", lista.stream().limit(100).toList());
        m.addAttribute("item", id == null ? null : produtos.buscar(loja, id));
        return "produtos";
    }

    @PostMapping("/produtos")
    public String salvar(@ModelAttribute ProdutoForm form, HttpSession s, RedirectAttributes r) {
        produtos.salvar(acesso.loja(s), form);
        r.addFlashAttribute("notice", "Produto salvo.");
        return "redirect:/produtos";
    }

    @PostMapping("/excluir/produto/{id}")
    public String excluir(@PathVariable long id, HttpSession s, RedirectAttributes r) {
        produtos.excluir(acesso.loja(s), id);
        r.addFlashAttribute("notice", "Produto excluído. Histórico preservado.");
        return "redirect:/produtos";
    }
}
