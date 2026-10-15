package br.com.estoque.controller.web;

import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EstornoController {
    private final EstoqueService estoque;
    private final HistoricoService historico;
    private final Acesso acesso;

    public EstornoController(EstoqueService estoque, HistoricoService historico, Acesso acesso) {
        this.estoque = estoque;
        this.historico = historico;
        this.acesso = acesso;
    }

    @GetMapping("/estorno")
    public String formulario(Model m, HttpSession s) {
        m.addAttribute("title", "Estornar movimentação");
        m.addAttribute("movimentos", historico.estornaveis(acesso.loja(s)));
        return "estorno";
    }

    @PostMapping("/estorno")
    public String salvar(
            @RequestParam long movementId,
            @RequestParam String reason,
            @RequestParam String requestKey,
            HttpSession s,
            RedirectAttributes r) {
        estoque.estornar(acesso.loja(s), movementId, reason, requestKey);
        r.addFlashAttribute("notice", "Estorno registrado.");
        return "redirect:/historico";
    }
}
