package br.com.estoque.controller.web;

import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapaController {
    private final PosicaoService posicoes;private final VinculoService vinculos;private final Acesso acesso;
    public MapaController(PosicaoService posicoes,VinculoService vinculos,Acesso acesso) {this.posicoes=posicoes;this.vinculos=vinculos;this.acesso=acesso;}
    @GetMapping("/mapa") public String mapa(Model m,HttpSession s) {return carregar(m,s,false);}
    @GetMapping("/ocupacao") public String ocupacao(Model m,HttpSession s) {return carregar(m,s,true);}
    private String carregar(Model m,HttpSession s,boolean ocupacao) {
        long loja=acesso.loja(s);if(ocupacao){acesso.exigir(loja,"relatorios");acesso.recurso(loja,"enterprise");}
        m.addAttribute("title",ocupacao?"Ocupação do estoque":"Mapa de posições");m.addAttribute("ocupacao",ocupacao);
        m.addAttribute("positions",posicoes.listar(loja));m.addAttribute("vinculos",vinculos.listar(loja));return "mapa";
    }
}
