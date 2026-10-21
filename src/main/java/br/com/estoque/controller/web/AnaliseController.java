package br.com.estoque.controller.web;

import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AnaliseController {
    private final AnaliseService analises;
    private final Acesso acesso;
    public AnaliseController(AnaliseService analises, Acesso acesso) { this.analises=analises; this.acesso=acesso; }

    @GetMapping("/fornecedores")
    public String fornecedores(@RequestParam(defaultValue="") String q, HttpSession session, Model model) {
        model.addAttribute("title", "Fornecedores externos");
        model.addAttribute("q", q);
        model.addAttribute("tabela", analises.consultar(acesso.loja(session), q));
        return "fornecedores";
    }

    @GetMapping("/relatorios")
    public String relatorios(@RequestParam(defaultValue="valor") String tipo,
            @RequestParam(required=false) LocalDate inicio, @RequestParam(required=false) LocalDate fim,
            HttpSession session, Model model) {
        var hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        if (inicio == null) inicio=hoje.withDayOfMonth(1);
        if (fim == null) fim=hoje;
        model.addAttribute("title", "Relatórios gerenciais");
        model.addAttribute("tipo", tipo); model.addAttribute("inicio", inicio); model.addAttribute("fim", fim);
        model.addAttribute("tabela", analises.relatorio(acesso.loja(session), tipo, inicio, fim));
        return "relatorios";
    }
}
