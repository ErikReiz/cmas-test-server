package com.cmasproject.cmastestserver.controller;

import com.cmasproject.cmastestserver.constants.ApplicationConstants;
import com.cmasproject.cmastestserver.constants.TestConstants;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.mapper.UserMapper;
import com.cmasproject.cmastestserver.model.LogInRequestDTO;
import com.cmasproject.cmastestserver.model.SignUpRequestDTO;
import com.cmasproject.cmastestserver.model.SignUpResponseDTO;
import com.cmasproject.cmastestserver.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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
    UserMapper userMapper;

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

        mockMvc.perform(post(TestConstants.SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].password", hasItems("Password is required", "Password must be from 6 to 64 characters long")))
                .andExpect(jsonPath("$[*].username", hasItems("Username is required", "Username must be between 3 and 50 characters")))
                .andExpect(jsonPath("$[*].email", hasItems("Email is required")))
                .andExpect(jsonPath("$[*].firstName", hasItems("First name is required")))
                .andExpect(jsonPath("$[*].lastName", hasItems("Last name is required")));

        verify(authService, never()).usernameExists(any(SignUpRequestDTO.class));
        verify(authService, never()).registerUser(any(SignUpRequestDTO.class));
        verify(userMapper, never()).userToSignUpResponseDTO(any(User.class));
    }

    @Test
    public void testDuplicateUserRegistration() throws Exception
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

        given(authService.usernameExists(signUpRequest)).willReturn(true);
        given(authService.emailExists(signUpRequest)).willReturn(true);
        given(authService.phoneNumberExists(signUpRequest)).willReturn(true);

        mockMvc.perform(post(TestConstants.SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.username", is("Username already exists.")))
                .andExpect(jsonPath("$.email", is("Email already exists.")))
                .andExpect(jsonPath("$['phone number']", is("Phone number already exists.")));

        verify(authService).usernameExists(any(SignUpRequestDTO.class));
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

        given(authService.usernameExists(signUpRequest)).willReturn(false);
        given(authService.emailExists(signUpRequest)).willReturn(false);
        given(authService.phoneNumberExists(signUpRequest)).willReturn(false);

        given(authService.registerUser(signUpRequest)).willReturn(savedUser);
        given(userMapper.userToSignUpResponseDTO(savedUser)).willReturn(mockResponse);

        mockMvc.perform(post(TestConstants.SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(authService).usernameExists(any(SignUpRequestDTO.class));
        verify(authService).registerUser(any(SignUpRequestDTO.class));
        verify(userMapper).userToSignUpResponseDTO(savedUser);
    }

    @Test
    public void testInvalidCredentialsLogin() throws Exception {
        LogInRequestDTO loginRequest = LogInRequestDTO.builder()
                .username("user")
                .password("password")
                .build();

        Authentication mockAuth = mock(Authentication.class);

        given(mockAuth.isAuthenticated()).willReturn(false);
        given(authService.authenticateUser(any(LogInRequestDTO.class))).willReturn(mockAuth);

        mockMvc.perform(post(TestConstants.LOG_IN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials."));

        verify(authService).authenticateUser(any(LogInRequestDTO.class));
        verify(mockAuth).isAuthenticated();
        verify(authService, never()).generateJWTToken(any(Authentication.class));
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        LogInRequestDTO loginRequest = LogInRequestDTO.builder()
                .username("user")
                .password("password")
                .build();

        Authentication mockAuth = mock(Authentication.class);
        String mockJwt = "mock-jwt-token";

        given(authService.authenticateUser(any(LogInRequestDTO.class))).willReturn(mockAuth);
        given(mockAuth.isAuthenticated()).willReturn(true);
        given(authService.generateJWTToken(mockAuth)).willReturn(mockJwt);

        mockMvc.perform(post(TestConstants.LOG_IN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string(ApplicationConstants.JWT_HEADER, is(mockJwt)));

        verify(authService).authenticateUser(any(LogInRequestDTO.class));
        verify(mockAuth).isAuthenticated();
        verify(authService).generateJWTToken(any(Authentication.class));
    }
}