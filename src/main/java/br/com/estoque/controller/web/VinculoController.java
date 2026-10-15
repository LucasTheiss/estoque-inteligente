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
    @GetMapping("/vinculos") public String listar(@RequestParam(defaultValue="")String q,@RequestParam(required=false)Long productId,@RequestParam(required=false)Long positionId,Model m,HttpSession s) {
        long loja=acesso.loja(s);acesso.exigir(loja,"cadastros");m.addAttribute("title","Produtos por posição");m.addAttribute("q",q);
        if((productId==null)!=(positionId==null))throw new IllegalArgumentException("Informe produto e posição do vínculo.");
        var item=productId==null?null:vinculos.buscar(loja,productId,positionId);
        var lista=new java.util.ArrayList<>(produtos.pesquisar(loja,q,0).stream().limit(100).toList());
        if(item!=null && lista.stream().noneMatch(p->p.produto().id().equals(productId)))lista.add(produtos.buscar(loja,productId));
        m.addAttribute("item",item);m.addAttribute("produtos",lista);m.addAttribute("positions",posicoes.listar(loja));m.addAttribute("vinculos",vinculos.listar(loja));return "vinculos";
    }
    @PostMapping("/vinculos") public String salvar(@RequestParam long productId,@RequestParam long positionId,@RequestParam(defaultValue="create")String action,@RequestParam(required=false)Long originalProductId,@RequestParam(required=false)Long originalPositionId,HttpSession s,RedirectAttributes r) {
        long loja=acesso.loja(s);
        switch(action) {
            case "create","delete" -> vinculos.salvar(loja,productId,positionId,action.equals("delete"));
            case "update" -> {
                if(originalProductId==null || originalPositionId==null)throw new IllegalArgumentException("Selecione o vínculo original.");
                vinculos.editar(loja,originalProductId,originalPositionId,productId,positionId);
            }
            default -> throw new IllegalArgumentException("Ação inválida.");
        }
        r.addFlashAttribute("notice","Vínculo atualizado.");return "redirect:/vinculos";
    }
}
