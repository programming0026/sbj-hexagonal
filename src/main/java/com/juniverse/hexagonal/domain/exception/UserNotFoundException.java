package com.juniverse.hexagonal.domain.exception;

/** No existe el usuario pedido (se responde 404). */
public class UserNotFoundException extends RuntimeException {
    
    /**
     * Crea la excepción.
     * @param message descripción del problema
     */
    public UserNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Crea la excepción con su causa.
     * @param message descripción del problema
     * @param cause causa original
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Crea la excepción para un id inexistente.
     * @param id identificador que no existe
     */
    public static UserNotFoundException withId(Long id) {
        return new UserNotFoundException("User not found with id: " + id);
    }
}