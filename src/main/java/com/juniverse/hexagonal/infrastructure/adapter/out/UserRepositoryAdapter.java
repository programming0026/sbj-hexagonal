package com.juniverse.hexagonal.infrastructure.adapter.out;

import com.juniverse.hexagonal.domain.entity.User;
import com.juniverse.hexagonal.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Adaptador de salida: implementa el puerto de persistencia con Spring Data JPA. */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    /** Inserta o actualiza. */
    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    /** Busca por id. */
    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    /** Todos los usuarios. */
    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    /** Indica si existe un usuario con ese correo. */
    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    /** Indica si otro usuario usa ese correo. */
    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return userJpaRepository.existsByEmailAndIdNot(email, id);
    }

    /** Elimina por id. */
    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }

    /** Indica si existe el usuario. */
    @Override
    public boolean existsById(Long id) {
        return userJpaRepository.existsById(id);
    }
}