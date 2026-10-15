package br.com.estoque.controller.web;

import br.com.estoque.dto.PosicaoForm;
import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PosicaoController {
    private final PosicaoService posicoes;
    private final Acesso acesso;

    public PosicaoController(PosicaoService posicoes, Acesso acesso) {
        this.posicoes = posicoes;
        this.acesso = acesso;
    }

    @GetMapping("/posicoes")
    public String listar(@RequestParam(required = false) Long id, Model m, HttpSession s) {
        long loja = acesso.loja(s);
        acesso.exigir(loja, "cadastros");
        m.addAttribute("title", "Posições");
        m.addAttribute("positions", posicoes.listar(loja));
        m.addAttribute("item", id == null ? null : posicoes.buscar(loja, id));
        return "posicoes";
    }

    @PostMapping("/posicoes")
    public String salvar(@ModelAttribute PosicaoForm form, HttpSession s, RedirectAttributes r) {
        posicoes.salvar(acesso.loja(s), form);
        r.addFlashAttribute("notice", "Posição salva.");
        return "redirect:/posicoes";
    }

    @PostMapping("/excluir/posicao/{id}")
    public String excluir(@PathVariable long id, HttpSession s, RedirectAttributes r) {
        posicoes.excluir(acesso.loja(s), id);
        r.addFlashAttribute("notice", "Posição excluída. Histórico preservado.");
        return "redirect:/posicoes";
    }
}
