package com.juniverse.hexagonal.domain.exception;

/** Ya existe un usuario con ese correo (se responde 409). */
public class UserAlreadyExistsException extends RuntimeException {
    
    /**
     * Crea la excepción.
     * @param message descripción del problema
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    /**
     * Crea la excepción con su causa.
     * @param message descripción del problema
     * @param cause causa original
     */
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Crea la excepción para un correo repetido.
     * @param email correo que ya existe
     */
    public static UserAlreadyExistsException withEmail(String email) {
        return new UserAlreadyExistsException("User already exists with email: " + email);
    }
}