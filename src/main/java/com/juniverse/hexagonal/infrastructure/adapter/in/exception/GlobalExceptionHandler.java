package com.juniverse.hexagonal.infrastructure.adapter.in.exception;

import com.juniverse.hexagonal.domain.exception.InvalidUserDataException;
import com.juniverse.hexagonal.domain.exception.UserAlreadyExistsException;
import com.juniverse.hexagonal.domain.exception.UserNotFoundException;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Convierte las excepciones en respuestas de error JSON uniformes ({@link ErrorResponseDTO}). */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** 404: el usuario no existe. */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        log.error("User not found: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /** 409: el correo ya existe. */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, HttpServletRequest request) {
        log.error("User already exists: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /** 400: datos de usuario inválidos. */
    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidUserDataException(
            InvalidUserDataException ex, HttpServletRequest request) {
        log.error("Invalid user data: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /** 400: falla la validación; incluye un detalle por campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Input validation failed")
                .details(details)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /** 400: el cuerpo no es un JSON válido. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnreadableBody(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed request body: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", "Malformed JSON request body", request);
    }

    /** 400: un parámetro de la URL tiene un tipo incorrecto (por ejemplo, un id no numérico). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Invalid parameter {}: {}", ex.getName(), ex.getValue());
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request",
                "Invalid value for parameter '" + ex.getName() + "'", request);
    }

    /** 409: se viola una restricción de la base de datos (por ejemplo, un correo duplicado en altas simultáneas). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        // Por ejemplo, dos altas simultáneas con el mismo correo: la comprobación previa no basta
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return buildError(HttpStatus.CONFLICT, "Conflict",
                "The operation violates a data integrity constraint (for example, a duplicate email)", request);
    }

    /** 500: error inesperado; no se expone el detalle. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Construye la respuesta de error estándar.
     *
     * @param status estado HTTP
     * @param error nombre corto del error
     * @param message mensaje para el cliente
     * @param request petición que originó el error
     * @return respuesta con el cuerpo {@link ErrorResponseDTO}
     */
    private ResponseEntity<ErrorResponseDTO> buildError(HttpStatus status, String error, String message,
            HttpServletRequest request) {
        ErrorResponseDTO body = ErrorResponseDTO.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
