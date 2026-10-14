package br.com.estoque.controller.web;

import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {
    private final Acesso acesso;private final ProdutoService produtos;private final VinculoService vinculos;
    public DashboardController(Acesso acesso,ProdutoService produtos,VinculoService vinculos) {this.acesso=acesso;this.produtos=produtos;this.vinculos=vinculos;}
    @GetMapping("/") public String dashboard(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="0")int page,@RequestParam(required=false)Long produto,Model m,HttpSession s) {
        if(acesso.usuario().superadmin() && s.getAttribute("loja")==null)return "redirect:/global";
        long loja=acesso.loja(s);
        var lista=produtos.pesquisar(loja,q,page);
        m.addAttribute("title","Estoque");m.addAttribute("q",q);m.addAttribute("page",page);m.addAttribute("hasNext",lista.size()>100);
        m.addAttribute("produtos",lista.stream().limit(100).toList());m.addAttribute("vinculos",vinculos.listar(loja));
        m.addAttribute("selecionado",produto!=null?produtos.buscar(loja,produto):lista.isEmpty()?null:lista.getFirst());
        return "dashboard";
    }
}
