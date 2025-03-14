package org.example.firstlabis.handler;


import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NonUniqueResultException;
import org.example.firstlabis.dto.error.ApiErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


/**
 * Класс обработчик исключений
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    public ResponseEntity<ApiErrorResponseDTO> handleValidationException(Exception ex, String description, HttpStatus status) {
        ApiErrorResponseDTO response = new ApiErrorResponseDTO(
                description,
                status.toString(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()));
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleHttpMessageConversionException(HttpMessageConversionException ex) {
        return handleValidationException(ex, "Invalid request: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleAuthenticationException(AuthenticationServiceException ex) {
        return handleValidationException(ex, "Cannot authenticate: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNoSuchElementExceptions(NoSuchElementException ex) {
        return handleValidationException(ex, "The object does not exist", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        return handleValidationException(ex, "The data you provided is not valid: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NonUniqueResultException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNonUniqueResultException(NonUniqueResultException ex) {
        return handleValidationException(ex, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleEntityNotFoundException(EntityNotFoundException ex) {
        return handleValidationException(ex, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Исключение отлавливает если не пошла валидация на каких-то полях доменных сущностей
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleAuthorizationDeniedException(Exception ex) {
        return handleValidationException(ex, "Недостаточно прав для доступа", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiErrorResponseDTO> handleOtherExceptions(Exception ex) {
        return handleValidationException(ex, "Неизвестная ошибка", HttpStatus.BAD_REQUEST);
    }


}
