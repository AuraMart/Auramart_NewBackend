package com.example.aura_mart.service;


import com.example.aura_mart.dto.AuthenticationResponse;
import com.example.aura_mart.dto.UserLoginDTO;
import com.example.aura_mart.model.User;

public interface AuthenticationService {
    AuthenticationResponse authenticate(UserLoginDTO loginDTO);
    AuthenticationResponse refreshToken(String refreshToken);
}