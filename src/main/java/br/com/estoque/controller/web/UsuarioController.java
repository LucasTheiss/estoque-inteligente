package br.com.estoque.controller.web;

import br.com.estoque.dto.UsuarioForm;
import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {
    private final UsuarioService usuarios;private final PermissaoService permissoes;private final Acesso acesso;
    public UsuarioController(UsuarioService usuarios,PermissaoService permissoes,Acesso acesso) {this.usuarios=usuarios;this.permissoes=permissoes;this.acesso=acesso;}
    @GetMapping("/usuarios") public String listar(@RequestParam(required=false)Long id,Model m,HttpSession s) {
        long loja=acesso.loja(s);m.addAttribute("title","Equipe e permissões");m.addAttribute("usuarios",usuarios.listar(loja));
        m.addAttribute("item",id==null?null:usuarios.buscar(loja,id));m.addAttribute("roles",Acesso.PERFIS);m.addAttribute("permissoes",Acesso.PERMISSOES);
        m.addAttribute("grants",permissoes.matriz(loja));return "usuarios";
    }
    @PostMapping("/usuarios") public String salvar(@ModelAttribute UsuarioForm form,HttpSession s,RedirectAttributes r) {
        usuarios.salvar(acesso.loja(s),form);r.addFlashAttribute("notice","Usuário atualizado.");return "redirect:/usuarios";
    }
    @PostMapping("/permissoes") public String permissoes(@RequestParam Map<String,String> campos,HttpSession s,RedirectAttributes r) {
        permissoes.atualizar(acesso.loja(s),campos.keySet());r.addFlashAttribute("notice","Permissões atualizadas.");return "redirect:/usuarios";
    }
}
