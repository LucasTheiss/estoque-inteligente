package br.com.estoque.controller.web;

import br.com.estoque.service.LojaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    private final LojaService lojas;

    public LoginController(LojaService lojas) {
        this.lojas = lojas;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("lojas", lojas.disponiveisLogin());
        model.addAttribute("title", "Entre na sua loja");
        return "login";
    }
}
