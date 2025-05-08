package com.workbeattalent.books.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalApiErrorExceptionHandler {

    @ExceptionHandler(exception = {EntityElementNotFoundException.class})
    public ResponseEntity<ApiError> entityElementNotFoundExceptionHandler(final EntityElementNotFoundException e, final WebRequest request) {
        ApiError apiError = new ApiError(
                request.getDescription(false),
                HttpStatus.NOT_FOUND.name(),
                e.getMessage(),
                Map.of());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(exception = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> methodArgumentValidationExceptionHandler(final MethodArgumentNotValidException e, final WebRequest request) {
        var errorMap = new HashMap<String, String>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            final var field = ((FieldError) error).getField();
            final var errorMessage = error.getDefaultMessage();
            errorMap.put(field, errorMessage);
        });
        var apiError = new ApiError(
                request.getDescription(false),
                HttpStatus.BAD_REQUEST.name(),
                e.getMessage(),
                errorMap);

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

}
