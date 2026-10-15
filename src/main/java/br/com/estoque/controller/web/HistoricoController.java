package br.com.estoque.controller.web;

import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HistoricoController {
    private final Acesso acesso;
    private final HistoricoService historico;
    private final RelatorioService relatorios;

    public HistoricoController(
            Acesso acesso, HistoricoService historico, RelatorioService relatorios) {
        this.acesso = acesso;
        this.historico = historico;
        this.relatorios = relatorios;
    }

    @GetMapping("/historico")
    public String historico(
            @RequestParam(defaultValue = "") String kind,
            @RequestParam(defaultValue = "") String from,
            @RequestParam(defaultValue = "") String to,
            Model m,
            HttpSession s) {
        long loja = acesso.loja(s);
        m.addAttribute("title", "Histórico");
        m.addAttribute("kind", kind);
        m.addAttribute("from", from);
        m.addAttribute("to", to);
        m.addAttribute("movimentos", historico.consultar(loja, kind, from, to));
        m.addAttribute("auditoria", historico.auditoria(loja));
        return "historico";
    }

    @GetMapping("/historico/exportar/{formato}")
    public ResponseEntity<byte[]> exportar(
            @PathVariable String formato,
            @RequestParam(defaultValue = "") String kind,
            @RequestParam(defaultValue = "") String from,
            @RequestParam(defaultValue = "") String to,
            HttpSession s)
            throws IOException {
        var arquivo = relatorios.exportar(acesso.loja(s), formato, kind, from, to);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(arquivo.tipo()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + arquivo.nome())
                .body(arquivo.conteudo());
    }
}
