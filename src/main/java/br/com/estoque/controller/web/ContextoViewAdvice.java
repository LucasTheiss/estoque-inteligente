package br.com.estoque.controller.web;

import br.com.estoque.config.SistemaConfig;
import br.com.estoque.security.UsuarioAutenticado;
import br.com.estoque.service.*;
import jakarta.servlet.http.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice(basePackages="br.com.estoque.controller.web")
public class ContextoViewAdvice {
    private final Acesso acesso;private final LojaService lojas;
    @Value("${app.demo}") boolean demo;
    public ContextoViewAdvice(Acesso acesso,LojaService lojas) {this.acesso=acesso;this.lojas=lojas;}
    @ModelAttribute public void contexto(Model model,Authentication auth,HttpSession session,HttpServletRequest request) {
        model.addAttribute("appName",SistemaConfig.getInstance().get("sistema.nome"));
        model.addAttribute("route",request.getRequestURI());model.addAttribute("key",UUID.randomUUID().toString());
        model.addAttribute("permissions",Set.of());model.addAttribute("user",null);model.addAttribute("tenant",null);model.addAttribute("demo",demo);
        if(request.getRequestURI().equals(request.getContextPath()+"/login"))return;
        if(auth!=null && auth.getPrincipal() instanceof UsuarioAutenticado) {
            var user=acesso.usuario();model.addAttribute("user",user);
            if(!user.superadmin() || session.getAttribute("loja")!=null) {
                long loja=acesso.loja(session);
                model.addAttribute("tenant",lojas.contexto(loja));model.addAttribute("permissions",acesso.permissoes(loja));
            }
        }
    }
}
