package com.example.aura_mart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT Authentication Response")
public class AuthenticationResponse {
    @Schema(description = "JWT access token")
    private String accessToken;

    @Schema(description = "User details")
    private UserResponseDTO userDetails;
}
