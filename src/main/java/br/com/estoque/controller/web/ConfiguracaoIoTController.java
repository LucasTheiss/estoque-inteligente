package br.com.estoque.controller.web;

import br.com.estoque.dto.IoTForm;
import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ConfiguracaoIoTController {
    private final ConfiguracaoIoTService configuracao;private final Acesso acesso;
    public ConfiguracaoIoTController(ConfiguracaoIoTService configuracao,Acesso acesso) {this.configuracao=configuracao;this.acesso=acesso;}
    @GetMapping("/iot/configuracao") public String formulario(Model m,HttpSession s) {
        m.addAttribute("title","Configuração IoT");m.addAttribute("item",configuracao.buscar(acesso.loja(s)));return "iot";
    }
    @PostMapping("/iot/configuracao") public String salvar(@ModelAttribute IoTForm form,HttpSession s,RedirectAttributes r) {
        configuracao.salvar(acesso.loja(s),form);r.addFlashAttribute("notice","Configuração salva.");return "redirect:/iot/configuracao";
    }
}
