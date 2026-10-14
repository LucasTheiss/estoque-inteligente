package br.com.estoque.controller.api;

import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(basePackages="br.com.estoque.controller.api")
@Order(0)
public class ErroApiAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> invalido(Exception ex) {return resposta(HttpStatus.BAD_REQUEST,ex.getMessage());}
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> proibido(Exception ex) {return resposta(HttpStatus.FORBIDDEN,ex.getMessage());}
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ProblemDetail> banco(Exception ex) {return resposta(HttpStatus.SERVICE_UNAVAILABLE,"Banco de dados indisponível. Tente novamente.");}
    private ResponseEntity<ProblemDetail> resposta(HttpStatus status,String detalhe) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(ProblemDetail.forStatusAndDetail(status,detalhe));
    }
}
