package br.com.estoque.databasefirst;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.jooq.exception.DataAccessException;

@Controller
public class ModelController {
    private final ModelService service;
    public ModelController(ModelService service) { this.service = service; }
    @GetMapping("/") String list(@RequestParam(required=false) Long id, Model model) {
        model.addAttribute("items", service.list());
        model.addAttribute("item", id == null ? null : service.get(id));
        return "models";
    }
    @PostMapping("/models") String save(@RequestParam(required=false) Long id, @RequestParam String name,
            @RequestParam(defaultValue="") String brand, @RequestParam(defaultValue="") String category,
            RedirectAttributes flash) {
        service.save(id, name, brand, category);
        flash.addFlashAttribute("notice", id == null ? "Modelo cadastrado no PostgreSQL." : "Modelo atualizado no PostgreSQL.");
        return "redirect:/";
    }
    @PostMapping("/models/{id}/delete") String delete(@PathVariable long id, RedirectAttributes flash) {
        service.delete(id); flash.addFlashAttribute("notice", "Modelo excluido do PostgreSQL.");
        return "redirect:/";
    }
    @ExceptionHandler(IllegalArgumentException.class)
    String invalid(IllegalArgumentException e, RedirectAttributes flash) {
        flash.addFlashAttribute("error", e.getMessage()); return "redirect:/";
    }
    @ExceptionHandler({DataIntegrityViolationException.class, DataAccessException.class})
    String conflict(RedirectAttributes flash) {
        flash.addFlashAttribute("error", "Nao foi possivel salvar. Verifique se nome, marca e categoria ja existem ou se ha vinculos.");
        return "redirect:/";
    }
}
