package com.example.aura_mart.service;



import com.example.aura_mart.dto.AuthenticationResponse;
import com.example.aura_mart.dto.UserSignupDTO;
import com.example.aura_mart.dto.UserResponseDTO;
import com.example.aura_mart.enums.UserRole;
import com.example.aura_mart.exception.ResourceAlreadyExistsException;
import com.example.aura_mart.exception.ResourceNotFoundException;
import com.example.aura_mart.model.User;
import com.example.aura_mart.repository.UserRepository;
import com.example.aura_mart.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthenticationResponse registerUser(UserSignupDTO registrationDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already in use");
        }

        // Create new user
        User user = new User();
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setUserRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());

        // Save user
        User savedUser = userRepository.save(user);

        System.out.println(savedUser);

        // Authenticate and generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registrationDTO.getEmail(),
                        registrationDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);


        // Convert user to response DTO
        UserResponseDTO userResponse = convertToUserResponseDTO(savedUser);

        // Return authentication response
        return new AuthenticationResponse(jwt, userResponse);
    }

    @Override
    public UserResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToUserResponseDTO(user);
    }


    @Override
    @Transactional
    public UserResponseDTO updateUserProfile(Long userId, UserSignupDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update user details
        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());

        // Only update password if provided
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return convertToUserResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUserAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }

    // Utility method to convert User to UserResponseDTO
    UserResponseDTO convertToUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setUserRole(user.getUserRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
