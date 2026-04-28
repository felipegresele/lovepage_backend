package com.loveapp.love_app_backend.config;

import com.mercadopago.exceptions.MPApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Erros de negócio conhecidos (ex: "Email já cadastrado") → 400 com mensagem amigável
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        log.warn("[ERROR] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    // ✅ NOVO — captura erros do Mercado Pago e loga o conteúdo real
    @ExceptionHandler(MPApiException.class)
    public ResponseEntity<?> handleMPApiException(MPApiException ex) {
        log.error("[MP ERROR] status={} content={}",
                ex.getStatusCode(),
                ex.getApiResponse() != null ? ex.getApiResponse().getContent() : "sem response");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        "error", "Erro ao processar pagamento no Mercado Pago",
                        "detalhe", ex.getApiResponse() != null ? ex.getApiResponse().getContent() : ex.getMessage()
                ));
    }

    // Erros inesperados → 500 genérico, sem stack trace para o cliente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        log.error("[ERROR] Erro interno inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Ocorreu um erro interno. Tente novamente mais tarde."));
    }
}