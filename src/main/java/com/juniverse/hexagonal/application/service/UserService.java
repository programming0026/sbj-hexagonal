package com.juniverse.hexagonal.application.service;

import com.juniverse.hexagonal.domain.entity.User;
import com.juniverse.hexagonal.domain.exception.UserAlreadyExistsException;
import com.juniverse.hexagonal.domain.exception.UserNotFoundException;
import com.juniverse.hexagonal.domain.port.in.UserServicePort;
import com.juniverse.hexagonal.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Servicio de aplicación: implementa los casos de uso de usuarios usando solo puertos (no conoce JPA ni HTTP). */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserServicePort {

    private final UserRepositoryPort userRepositoryPort;

    /**
     * Crea un usuario.
     * @param user datos del usuario
     * @return el usuario guardado
     * @throws UserAlreadyExistsException si el correo ya existe
     */
    @Override
    public User createUser(User user) {
        log.info("Creating user with email: {}", user.getEmail());
        
        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw UserAlreadyExistsException.withEmail(user.getEmail());
        }
        
        User savedUser = userRepositoryPort.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Busca un usuario.
     * @param id identificador
     * @return el usuario
     * @throws UserNotFoundException si no existe
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> UserNotFoundException.withId(id));
    }

    /** Lista todos los usuarios. */
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepositoryPort.findAll();
    }

    /**
     * Actualiza un usuario conservando su fecha de creación.
     * @param id identificador
     * @param user nuevos datos
     * @return el usuario actualizado
     * @throws UserNotFoundException si no existe
     * @throws UserAlreadyExistsException si el correo pertenece a otro usuario
     */
    @Override
    public User updateUser(Long id, User user) {
        log.info("Updating user with id: {}", id);
        
        if (!userRepositoryPort.existsById(id)) {
            throw UserNotFoundException.withId(id);
        }
        
        if (userRepositoryPort.existsByEmailAndIdNot(user.getEmail(), id)) {
            throw UserAlreadyExistsException.withEmail(user.getEmail());
        }
        
        user.setId(id);
        // El usuario llega desde un DTO, sin fecha de creación: se conserva la original (si no, la respuesta traía createdAt nulo)
        userRepositoryPort.findById(id).ifPresent(existing -> user.setCreatedAt(existing.getCreatedAt()));
        User updatedUser = userRepositoryPort.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());
        return updatedUser;
    }

    /**
     * Elimina un usuario.
     * @param id identificador
     * @throws UserNotFoundException si no existe
     */
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        
        if (!userRepositoryPort.existsById(id)) {
            throw UserNotFoundException.withId(id);
        }
        
        userRepositoryPort.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }
}