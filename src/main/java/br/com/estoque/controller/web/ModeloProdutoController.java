package br.com.estoque.controller.web;

import br.com.estoque.dto.ModeloProdutoForm;
import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ModeloProdutoController {
    private final ModeloProdutoService modelos;
    private final Acesso acesso;
    public ModeloProdutoController(ModeloProdutoService modelos,Acesso acesso) { this.modelos=modelos; this.acesso=acesso; }
    @GetMapping("/modelos") public String listar(@RequestParam(required=false)Long id,Model m,HttpSession s) {
        long loja=acesso.loja(s);
        m.addAttribute("title","Modelos de produto"); m.addAttribute("modelos",modelos.listar(loja));
        m.addAttribute("item",id==null?null:modelos.buscar(loja,id)); return "modelos";
    }
    @PostMapping("/modelos") public String salvar(@ModelAttribute ModeloProdutoForm form,HttpSession s,RedirectAttributes r) {
        modelos.salvar(acesso.loja(s),form); r.addFlashAttribute("notice","Modelo salvo."); return "redirect:/modelos";
    }
    @PostMapping("/excluir/modelo/{id}") public String excluir(@PathVariable long id,HttpSession s,RedirectAttributes r) {
        modelos.excluir(acesso.loja(s),id); r.addFlashAttribute("notice","Modelo excluído."); return "redirect:/modelos";
    }
}
