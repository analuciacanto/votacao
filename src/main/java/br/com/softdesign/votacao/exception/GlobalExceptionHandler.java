package br.com.softdesign.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<String> mensagens = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("mensagens", mensagens);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(SessaoVotacaoInvaalidaException.class)
    public ResponseEntity<Map<String, Object>> handleSessaoVotacaoInvalida(SessaoVotacaoInvaalidaException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("mensagem", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(VotoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleVotoInvalido(VotoInvalidoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("mensagem", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
