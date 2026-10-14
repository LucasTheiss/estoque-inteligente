package br.com.estoque.controller.api;

import br.com.estoque.service.SaudeService;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class SaudeController {
    private final SaudeService saude;
    public SaudeController(SaudeService saude) {this.saude=saude;}
    @GetMapping(value="/health",produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> health() {
        boolean ok=saude.disponivel();
        return ResponseEntity.status(ok?200:503).contentType(MediaType.APPLICATION_JSON).body(Map.of("status",ok?"ok":"unavailable"));
    }
}
