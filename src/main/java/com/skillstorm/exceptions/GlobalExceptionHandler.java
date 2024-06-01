package com.skillstorm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle requests for resources that do not exist:
    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorMessage>> handleNotFoundExceptions(IllegalArgumentException e) {
        ErrorMessage error = new ErrorMessage();
        error.setCode(HttpStatus.NOT_FOUND.value());
        error.setMessage(e.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    // Handle Bad Requests from trying to add or update entities with invalid data in the RequestBody:
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorMessage>> handleValidationExceptions(MethodArgumentNotValidException e) {
        ErrorMessage error = new ErrorMessage();
        error.setCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).collect(Collectors.joining(", ")));

        return Mono.just(ResponseEntity.badRequest().body(error));
    }

}
