package com.cmasproject.cmastestserver.controller;

import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.mapper.UserMapper;
import com.cmasproject.cmastestserver.model.SignUpRequestDTO;
import com.cmasproject.cmastestserver.model.SignUpResponseDTO;
import com.cmasproject.cmastestserver.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;
    @MockitoBean
    private UserMapper userMapper;

    @Test
    public void testInvalidSignUpRequest() throws Exception
    {
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username("")
                .password("")
                .dateOfBirth(LocalDate.parse("2026-05-15"))
                .firstName("")
                .lastName("")
                .email("")
                .phoneNumber("")
                .build();

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].password", hasItems("Password is required", "Password must be from 6 to 64 characters long")))
                .andExpect(jsonPath("$[*].username", hasItems("Username is required", "Username must be between 3 and 50 characters")))
                .andExpect(jsonPath("$[*].email", hasItems("Email is required")))
                .andExpect(jsonPath("$[*].firstName", hasItems("First name is required")))
                .andExpect(jsonPath("$[*].lastName", hasItems("Last name is required")));

        verify(authService, never()).userExists(any(SignUpRequestDTO.class));
        verify(authService, never()).registerUser(any(SignUpRequestDTO.class));
        verify(userMapper, never()).userToSignUpResponseDTO(any(User.class));
    }

    @Test
    public void testDuplicateUserRegistration() throws Exception
    {
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1234567890")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();

        given(authService.userExists(signUpRequestDTO)).willReturn(Boolean.TRUE);

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDTO))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists."));

        verify(authService).userExists(any(SignUpRequestDTO.class));
        verify(authService, never()).registerUser(any(SignUpRequestDTO.class));
        verify(userMapper, never()).userToSignUpResponseDTO(any(User.class));
    }

    @Test
    public void testSuccessfulUserRegistration() throws Exception
    {
        SignUpRequestDTO signUpRequest = SignUpRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1234567890")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();

        User savedUser = User.builder()
                .username(signUpRequest.getUsername())
                .build();

        SignUpResponseDTO mockResponse = SignUpResponseDTO.builder()
                .username(savedUser.getUsername())
                .build();

        given(authService.userExists(signUpRequest)).willReturn(false);
        given(authService.registerUser(signUpRequest)).willReturn(savedUser);
        given(userMapper.userToSignUpResponseDTO(savedUser)).willReturn(mockResponse);

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(authService).userExists(any(SignUpRequestDTO.class));
        verify(authService).registerUser(any(SignUpRequestDTO.class));
        verify(userMapper).userToSignUpResponseDTO(savedUser);
    }
}