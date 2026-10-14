package br.com.estoque.controller.web;

import br.com.estoque.dto.LojaForm;
import br.com.estoque.service.LojaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LojaController {
    private final LojaService lojas;
    public LojaController(LojaService lojas) {this.lojas=lojas;}
    @GetMapping("/global") public String listar(@RequestParam(required=false)Long id,Model m) {
        m.addAttribute("title","Todas as lojas");m.addAttribute("lojas",lojas.listar());m.addAttribute("item",id==null?null:lojas.editar(id));return "global";
    }
    @PostMapping("/global/lojas") public String salvar(@ModelAttribute LojaForm form,RedirectAttributes r) {
        lojas.salvar(form);r.addFlashAttribute("notice","Loja salva com sucesso.");return "redirect:/global";
    }
    @PostMapping("/global/selecionar") public String selecionar(@RequestParam long id,HttpSession session) {
        session.setAttribute("loja",lojas.selecionar(id));return "redirect:/";
    }
}
