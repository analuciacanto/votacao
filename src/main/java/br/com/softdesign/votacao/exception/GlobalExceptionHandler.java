package br.com.softdesign.votacao.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {


        List<String> camposInvalidos = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .toList();

        log.warn("Erro de validação de request | camposInvalidos={}", camposInvalidos);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("mensagens", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(SessaoVotacaoInvaalidaException.class)
    public ResponseEntity<Map<String, Object>> handleSessaoVotacaoInvalida(SessaoVotacaoInvaalidaException ex) {

        log.warn("Regra de negócio violada | tipo=SessaoVotacao | motivo={}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(VotoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleVotoInvalido(VotoInvalidoException ex) {

        log.warn("Regra de negócio violada | tipo=Voto | motivo={}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("mensagem", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex) {

        log.warn("Content-Type não suportado | recebido={}", ex.getContentType());

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        response.put("mensagem", "Content-Type não suportado");

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleErroInesperado(Exception ex) {

        log.error("Erro inesperado no sistema", ex);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("mensagem", "Erro interno do servidor");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
