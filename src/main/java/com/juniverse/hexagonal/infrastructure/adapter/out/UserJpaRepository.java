package com.juniverse.hexagonal.infrastructure.adapter.out;

import com.juniverse.hexagonal.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositorio Spring Data JPA de usuarios. */
@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    
    /** Indica si existe un usuario con ese correo. */
    boolean existsByEmail(String email);
    
    /** Indica si otro usuario usa ese correo. */
    boolean existsByEmailAndIdNot(String email, Long id);
}