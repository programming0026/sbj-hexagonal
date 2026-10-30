package com.juniverse.hexagonal.domain.exception;

/** Los datos del usuario no son válidos (se responde 400). */
public class InvalidUserDataException extends RuntimeException {
    
    /**
     * Crea la excepción.
     * @param message descripción del problema
     */
    public InvalidUserDataException(String message) {
        super(message);
    }
    
    /**
     * Crea la excepción con su causa.
     * @param message descripción del problema
     * @param cause causa original
     */
    public InvalidUserDataException(String message, Throwable cause) {
        super(message, cause);
    }
}