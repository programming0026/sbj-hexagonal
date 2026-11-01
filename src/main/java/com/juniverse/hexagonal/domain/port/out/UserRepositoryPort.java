package com.juniverse.hexagonal.domain.port.out;

import com.juniverse.hexagonal.domain.entity.User;

import java.util.List;
import java.util.Optional;

/** Puerto de salida: lo que la aplicación necesita de la persistencia. */
public interface UserRepositoryPort {
    
    /**
     * Inserta o actualiza.
     * @param user usuario
     */
    User save(User user);
    
    /**
     * Busca por id.
     * @param id identificador
     */
    Optional<User> findById(Long id);
    
    /** Todos los usuarios. */
    List<User> findAll();
    
    boolean existsByEmail(String email);
    
    /**
     * Indica si otro usuario (con id distinto) usa el correo.
     * @param email correo
     * @param id id a excluir
     */
    boolean existsByEmailAndIdNot(String email, Long id);
    
    /**
     * Elimina por id.
     * @param id identificador
     */
    void deleteById(Long id);
    
    boolean existsById(Long id);
}