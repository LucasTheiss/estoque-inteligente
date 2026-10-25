package br.com.estoque.controller.web;

import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OfertaController {
    private final OfertaService ofertas;
    private final Acesso acesso;
    public OfertaController(OfertaService ofertas, Acesso acesso) { this.ofertas=ofertas; this.acesso=acesso; }
    @GetMapping("/ofertas")
    public String consultar(@RequestParam(defaultValue="") String gtin, @RequestParam(defaultValue="") String fornecedor,
            HttpSession session, Model model) { return tela(gtin, fornecedor, session, model, false); }
    @GetMapping("/comparacao-precos")
    public String comparar(@RequestParam(defaultValue="") String gtin, @RequestParam(defaultValue="") String fornecedor,
            HttpSession session, Model model) { return tela(gtin, fornecedor, session, model, true); }
    private String tela(String gtin, String fornecedor, HttpSession session, Model model, boolean comparar) {
        long loja = acesso.loja(session);
        model.addAttribute("title", comparar ? "Comparação de preços" : "Ofertas externas");
        model.addAttribute("comparar", comparar);
        model.addAttribute("gtin", gtin); model.addAttribute("fornecedor", fornecedor);
        model.addAttribute("tabela", comparar ? ofertas.comparar(loja,gtin,fornecedor) : ofertas.consultar(loja,gtin,fornecedor));
        return "ofertas";
    }
}
