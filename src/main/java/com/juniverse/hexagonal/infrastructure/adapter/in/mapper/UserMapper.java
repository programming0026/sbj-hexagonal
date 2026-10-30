package com.juniverse.hexagonal.infrastructure.adapter.in.mapper;

import com.juniverse.hexagonal.domain.entity.User;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserCreateRequestDTO;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserResponseDTO;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserUpdateRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/** Convierte entre DTOs y la entidad {@code User}. */
@Component
public class UserMapper {

    /**
     * De DTO de alta a entidad.
     * @param dto datos de alta
     */
    public User toEntity(UserCreateRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        return user;
    }

    /**
     * De DTO de actualización a entidad.
     * @param dto datos de actualización
     */
    public User toEntity(UserUpdateRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        return user;
    }

    /**
     * De entidad a DTO de respuesta.
     * @param user usuario
     */
    public UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Convierte una lista de usuarios.
     * @param users usuarios
     */
    public List<UserResponseDTO> toResponseDTOList(List<User> users) {
        return users.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}