package com.helen.api_crm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.servlet.function.ServerResponse.status;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<String> handleClientNotFound(ClientNotFoundException ex){
        return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex){

            Map<String, String> errors = new HashMap<>();

            ex.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));

            return ResponseEntity.badRequest().body(errors);
    }
}
