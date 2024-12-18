package com.example.aura_mart.service;

import com.example.aura_mart.dto.AuthenticationResponse;
import com.example.aura_mart.dto.UserLoginDTO;
import com.example.aura_mart.dto.UserResponseDTO;
import com.example.aura_mart.model.User;
import com.example.aura_mart.repository.UserRepository;
import com.example.aura_mart.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceImpl userService;

    @Override
    public AuthenticationResponse authenticate(UserLoginDTO loginDTO) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );

            // Set authentication in context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Find user
            User user = userRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT token
            String jwt = jwtTokenProvider.generateToken(authentication);

            // Convert user to response DTO
            UserResponseDTO userResponse = userService.convertToUserResponseDTO(user);

            // Return authentication response
            return new AuthenticationResponse(jwt, userResponse);
        } catch (Exception e) {
            // More specific error handling
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public AuthenticationResponse refreshToken(String refreshToken) {
        // Validate and refresh token logic
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create Authentication object with user details
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    user.getPassword(),
                    // Pass authorities if needed
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            );

            String newAccessToken = jwtTokenProvider.generateToken(authentication);

            return new AuthenticationResponse(
                    newAccessToken,
                    userService.convertToUserResponseDTO(user)
            );
        }
        throw new RuntimeException("Invalid or expired refresh token");
    }
}