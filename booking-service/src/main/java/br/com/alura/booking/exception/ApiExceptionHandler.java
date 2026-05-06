package br.com.alura.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RegraDeNegocioException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, String> handleRegraDeNegocio(RegraDeNegocioException ex) {
        return Map.of("erro", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneric(Exception ex) {
        return Map.of("erro", "Erro inesperado: " + ex.getMessage());
    }
}
