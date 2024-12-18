package com.example.aura_mart.service;


import com.example.aura_mart.dto.AuthenticationResponse;
import com.example.aura_mart.dto.UserSignupDTO;
import com.example.aura_mart.dto.UserResponseDTO;

public interface UserService {
    AuthenticationResponse registerUser(UserSignupDTO registrationDTO);
    UserResponseDTO getUserProfile(Long userId);
    UserResponseDTO updateUserProfile(Long userId, UserSignupDTO updateDTO);
    void deleteUserAccount(Long userId);
}