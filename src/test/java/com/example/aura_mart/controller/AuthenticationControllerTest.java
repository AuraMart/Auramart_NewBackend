package com.example.aura_mart.controller;

import com.example.aura_mart.dto.*;
import com.example.aura_mart.enums.UserRole;
import com.example.aura_mart.service.AuthenticationService;
import com.example.aura_mart.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Prepare test data
        UserSignupDTO signupDTO = new UserSignupDTO(
                "John",
                "Doe",
                "john.doe@example.com",
                "StrongPassword123!"
        );

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                UserRole.USER,
                LocalDateTime.now()
        );

        AuthenticationResponse authResponse = new AuthenticationResponse(
                "mock.jwt.token",
                userResponseDTO
        );

        // Mock service method
        when(userService.registerUser(any(UserSignupDTO.class)))
                .thenReturn(authResponse);

        // Perform request and validate
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                .andExpect(jsonPath("$.userDetails.firstName").value("John"))
                .andExpect(jsonPath("$.userDetails.lastName").value("Doe"))
                .andExpect(jsonPath("$.userDetails.email").value("john.doe@example.com"));
    }

    @Test
    void testRegisterUser_Validation_Failure() throws Exception {
        // Prepare invalid test data
        UserSignupDTO invalidSignupDTO = new UserSignupDTO(
                "J",
                "",
                "invalid-email",
                "short"
        );

        // Perform request and validate validation errors
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSignupDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_Success() throws Exception {
        // Prepare test data
        UserLoginDTO loginDTO = new UserLoginDTO(
                "john.doe@example.com",
                "StrongPassword123!"
        );

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                UserRole.USER,
                LocalDateTime.now()
        );

        AuthenticationResponse authResponse = new AuthenticationResponse(
                "mock.jwt.token",
                userResponseDTO
        );

        // Mock service method
        when(authenticationService.authenticate(any(UserLoginDTO.class)))
                .thenReturn(authResponse);

        // Perform request and validate
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                .andExpect(jsonPath("$.userDetails.firstName").value("John"))
                .andExpect(jsonPath("$.userDetails.lastName").value("Doe"))
                .andExpect(jsonPath("$.userDetails.email").value("john.doe@example.com"));
    }

    @Test
    void testLogin_Validation_Failure() throws Exception {
        // Prepare invalid test data
        UserLoginDTO invalidLoginDTO = new UserLoginDTO(
                "invalid-email",
                ""
        );

        // Perform request and validate validation errors
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        // Prepare test data
        String refreshToken = "Bearer old.jwt.token";

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                UserRole.USER,
                LocalDateTime.now()
        );

        AuthenticationResponse authResponse = new AuthenticationResponse(
                "new.jwt.token",
                userResponseDTO
        );

        // Mock service method
        when(authenticationService.refreshToken(refreshToken))
                .thenReturn(authResponse);

        // Perform request and validate
        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new.jwt.token"))
                .andExpect(jsonPath("$.userDetails.firstName").value("John"))
                .andExpect(jsonPath("$.userDetails.lastName").value("Doe"))
                .andExpect(jsonPath("$.userDetails.email").value("john.doe@example.com"));
    }
}
