package com.example.aura_mart.controller;

import com.example.aura_mart.dto.AuthenticationResponse;
import com.example.aura_mart.dto.UserLoginDTO;
import com.example.aura_mart.dto.UserSignupDTO;
import com.example.aura_mart.dto.UserResponseDTO;
import com.example.aura_mart.service.AuthenticationService;
import com.example.aura_mart.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management endpoints")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthenticationResponse> registerUser(
            @Valid @RequestBody UserSignupDTO registrationDTO
    ) {
        return ResponseEntity.ok(userService.registerUser(registrationDTO));
    }

    @PostMapping("/signin")
    @Operation(summary = "Authenticate user and return JWT token")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody UserLoginDTO loginDTO
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(loginDTO));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @RequestHeader("Authorization") String refreshToken
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshToken));
    }
}