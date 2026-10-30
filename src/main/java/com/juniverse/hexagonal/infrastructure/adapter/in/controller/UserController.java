package com.juniverse.hexagonal.infrastructure.adapter.in.controller;

import com.juniverse.hexagonal.domain.entity.User;
import com.juniverse.hexagonal.domain.port.in.UserServicePort;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.ErrorResponseDTO;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserCreateRequestDTO;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserResponseDTO;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserUpdateRequestDTO;
import com.juniverse.hexagonal.infrastructure.adapter.in.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Adaptador de entrada REST: expone los casos de uso de usuarios en {@code /v1/users}. */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management operations")
public class UserController {

    private final UserServicePort userServicePort;
    private final UserMapper userMapper;

    @Operation(summary = "Create a new user", description = "Creates a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "User already exists with this email",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    /**
     * Crea un usuario (201).
     * @param request datos validados
     * @return el usuario creado
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateRequestDTO request) {
        log.info("POST /v1/users - Creating user with email: {}", request.getEmail());
        
        User user = userMapper.toEntity(request);
        User createdUser = userServicePort.createUser(user);
        UserResponseDTO response = userMapper.toResponseDTO(createdUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    /**
     * Obtiene un usuario.
     * @param id identificador
     * @return el usuario
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /v1/users/{} - Fetching user", id);
        
        User user = userServicePort.getUserById(id);
        UserResponseDTO response = userMapper.toResponseDTO(user);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
    })
    /** Lista los usuarios. */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("GET /v1/users - Fetching all users");
        
        List<User> users = userServicePort.getAllUsers();
        List<UserResponseDTO> response = userMapper.toResponseDTOList(users);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Email already exists for another user",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    /**
     * Actualiza un usuario.
     * @param id identificador
     * @param request nuevos datos validados
     * @return el usuario actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id, 
            @Valid @RequestBody UserUpdateRequestDTO request) {
        log.info("PUT /v1/users/{} - Updating user", id);
        
        User user = userMapper.toEntity(request);
        User updatedUser = userServicePort.updateUser(id, user);
        UserResponseDTO response = userMapper.toResponseDTO(updatedUser);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete user", description = "Deletes a user from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    /**
     * Elimina un usuario (204).
     * @param id identificador
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /v1/users/{} - Deleting user", id);
        
        userServicePort.deleteUser(id);
        
        return ResponseEntity.noContent().build();
    }
}