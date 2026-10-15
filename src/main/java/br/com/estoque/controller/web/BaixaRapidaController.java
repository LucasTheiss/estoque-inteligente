package br.com.estoque.controller.web;

import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BaixaRapidaController {
    private final VinculoService vinculos;
    private final Acesso acesso;

    public BaixaRapidaController(VinculoService vinculos, Acesso acesso) {
        this.vinculos = vinculos;
        this.acesso = acesso;
    }

    @GetMapping("/baixa-rapida")
    public String formulario(Model m, HttpSession s) {
        long loja = acesso.loja(s);
        acesso.exigir(loja, "saida");
        m.addAttribute("title", "Baixa rápida");
        m.addAttribute("vinculos", vinculos.listar(loja));
        return "rapida";
    }
}
