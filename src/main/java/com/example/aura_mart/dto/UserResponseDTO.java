package com.example.aura_mart.dto;

import com.example.aura_mart.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Response DTO")
public class UserResponseDTO {
    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User role", example = "BUYER")
    private UserRole userRole;

    @Schema(description = "Account creation date")
    private LocalDateTime createdAt;
}
