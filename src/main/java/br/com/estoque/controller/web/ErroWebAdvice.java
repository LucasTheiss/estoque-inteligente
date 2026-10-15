package br.com.estoque.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import java.time.DateTimeException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackages = "br.com.estoque.controller.web")
@Order(1)
public class ErroWebAdvice {
    @ExceptionHandler({
        IllegalArgumentException.class,
        DateTimeException.class,
        BindException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ModelAndView invalido(Exception ex, HttpServletRequest req) {
        return erro(
                HttpStatus.BAD_REQUEST,
                ex instanceof BindException || ex instanceof MethodArgumentTypeMismatchException
                        ? "Verifique os números e campos informados."
                        : ex.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ModelAndView banco(Exception ex) {
        return erro(
                HttpStatus.CONFLICT,
                "Não foi possível salvar. Verifique dados duplicados ou vínculos existentes.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView proibido(Exception ex) {
        return erro(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    private ModelAndView erro(HttpStatus status, String mensagem) {
        var view = new ModelAndView("erro");
        view.setStatus(status);
        view.addObject("message", mensagem);
        view.addObject("status", status.value());
        return view;
    }
}
