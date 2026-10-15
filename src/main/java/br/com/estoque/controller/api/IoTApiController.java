package br.com.estoque.controller.api;

import br.com.estoque.service.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/iot", produces = MediaType.APPLICATION_JSON_VALUE)
public class IoTApiController {
    private final Acesso acesso;
    private final IoTService iot;

    public IoTApiController(Acesso acesso, IoTService iot) {
        this.acesso = acesso;
        this.iot = iot;
    }

    @GetMapping("/status")
    public Map<String, Object> status(HttpSession s) {
        return iot.status(acesso.loja(s));
    }

    @PostMapping("/sinalizar/{id}")
    public Map<String, Object> sinalizar(@PathVariable long id, HttpSession s) {
        return iot.sinalizar(acesso.loja(s), id);
    }

    @GetMapping("/confirmacao/{id}")
    public Map<String, Object> confirmacao(@PathVariable long id, HttpSession s) {
        return iot.confirmacao(acesso.loja(s), id);
    }

    @GetMapping("/balanca/{produto}/{device}")
    public Map<String, Object> peso(
            @PathVariable long produto, @PathVariable String device, HttpSession s) {
        return iot.peso(acesso.loja(s), produto, device);
    }
}
