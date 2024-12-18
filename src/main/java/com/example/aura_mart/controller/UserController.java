package com.example.aura_mart.controller;

import com.example.aura_mart.dto.UserSignupDTO;
import com.example.aura_mart.dto.UserResponseDTO;
import com.example.aura_mart.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User profile management endpoints")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile/{userId}")
    @Operation(summary = "Get user profile by user ID")
    public ResponseEntity<UserResponseDTO> getUserProfile(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PutMapping("/profile/{userId}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<UserResponseDTO> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserSignupDTO updateDTO
    ) {
        return ResponseEntity.ok(userService.updateUserProfile(userId, updateDTO));
    }

    @DeleteMapping("/profile/{userId}")
    @Operation(summary = "Delete user account")
    public ResponseEntity<Void> deleteUserAccount(
            @PathVariable Long userId
    ) {
        userService.deleteUserAccount(userId);
        return ResponseEntity.noContent().build();
    }
}