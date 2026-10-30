package com.juniverse.hexagonal.infrastructure.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** Cuerpo de las respuestas de error. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response DTO containing error information")
public class ErrorResponseDTO {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type", example = "Bad Request")
    private String error;

    @Schema(description = "Error message", example = "Validation failed")
    private String message;

    @Schema(description = "Detailed error information", example = "[\"name: Name is required\", \"email: Email should be valid\"]")
    private List<String> details;

    @Schema(description = "Request path that caused the error", example = "/api/v1/users")
    private String path;

    @Schema(description = "Timestamp when the error occurred", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
}