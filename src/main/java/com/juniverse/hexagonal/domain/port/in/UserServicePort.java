package com.juniverse.hexagonal.domain.port.in;

import com.juniverse.hexagonal.domain.entity.User;

import java.util.List;

/** Puerto de entrada: lo que la aplicación ofrece al exterior (casos de uso de usuarios). */
public interface UserServicePort {
    
    /**
     * Crea un usuario.
     * @param user datos
     * @return el usuario creado
     */
    User createUser(User user);
    
    /**
     * Busca un usuario.
     * @param id identificador
     */
    User getUserById(Long id);
    
    /** Lista los usuarios. */
    List<User> getAllUsers();
    
    /**
     * Actualiza un usuario.
     * @param id identificador
     * @param user nuevos datos
     */
    User updateUser(Long id, User user);
    
    /**
     * Elimina un usuario.
     * @param id identificador
     */
    void deleteUser(Long id);
}