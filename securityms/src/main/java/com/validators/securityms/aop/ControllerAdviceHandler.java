package com.validators.securityms.aop;


import com.validators.securityms.exception.CustomException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdviceHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> exception(CustomException e) {


        return switch (e.getMessage()) {
            case "Http exception" -> {
                HttpClientErrorException ex = (HttpClientErrorException) e.getException();
                String responseBody = ex.getResponseBodyAsString();
                HttpStatus statusCode = (HttpStatus) ex.getStatusCode();
                yield new ResponseEntity<>(responseBody,statusCode);
            }
            case "Server not available" -> new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
            case "Bad credentials" -> new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
            default -> new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception e) {

        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
