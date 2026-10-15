package br.com.estoque.controller.web;

import br.com.estoque.dto.MovimentoForm;
import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MovimentoController {
    private final EstoqueService estoque;
    private final VinculoService vinculos;
    private final Acesso acesso;

    public MovimentoController(EstoqueService estoque, VinculoService vinculos, Acesso acesso) {
        this.estoque = estoque;
        this.vinculos = vinculos;
        this.acesso = acesso;
    }

    @GetMapping("/movimentar")
    public String formulario(Model m, HttpSession s) {
        long loja = acesso.loja(s);
        acesso.exigir(loja, "consultar");
        m.addAttribute("title", "Movimentar estoque");
        m.addAttribute("vinculos", vinculos.listar(loja));
        return "movimento";
    }

    @PostMapping("/movimentar")
    public String salvar(@ModelAttribute MovimentoForm form, HttpSession s, RedirectAttributes r) {
        estoque.movimentar(acesso.loja(s), form);
        r.addFlashAttribute("notice", "Movimentação registrada.");
        return "redirect:/";
    }
}
