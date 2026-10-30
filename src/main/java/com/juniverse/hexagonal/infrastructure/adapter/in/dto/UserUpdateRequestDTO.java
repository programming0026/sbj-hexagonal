package com.juniverse.hexagonal.infrastructure.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Datos para actualizar un usuario. */
@Data
@Schema(description = "Request DTO for updating an existing user")
public class UserUpdateRequestDTO {

    @Schema(description = "User's full name", example = "Jane Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Schema(description = "User's email address", example = "jane.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Schema(description = "User's phone number", example = "+1987654321", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 15, message = "Phone must not exceed 15 characters")
    private String phone;
}