package br.com.estoque.controller.web;

import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VinculoController {
    private final VinculoService vinculos;private final ProdutoService produtos;private final PosicaoService posicoes;private final Acesso acesso;
    public VinculoController(VinculoService vinculos,ProdutoService produtos,PosicaoService posicoes,Acesso acesso) {this.vinculos=vinculos;this.produtos=produtos;this.posicoes=posicoes;this.acesso=acesso;}
    @GetMapping("/vinculos") public String listar(@RequestParam(defaultValue="")String q,Model m,HttpSession s) {
        long loja=acesso.loja(s);acesso.exigir(loja,"cadastros");m.addAttribute("title","Produtos por posição");m.addAttribute("q",q);
        m.addAttribute("produtos",produtos.pesquisar(loja,q,0).stream().limit(100).toList());m.addAttribute("positions",posicoes.listar(loja));m.addAttribute("vinculos",vinculos.listar(loja));return "vinculos";
    }
    @PostMapping("/vinculos") public String salvar(@RequestParam long productId,@RequestParam long positionId,@RequestParam(defaultValue="create")String action,HttpSession s,RedirectAttributes r) {
        vinculos.salvar(acesso.loja(s),productId,positionId,action.equals("delete"));r.addFlashAttribute("notice","Vínculo atualizado.");return "redirect:/vinculos";
    }
}
