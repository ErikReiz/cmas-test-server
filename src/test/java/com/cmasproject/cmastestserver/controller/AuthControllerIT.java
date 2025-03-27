package com.cmasproject.cmastestserver.controller;

import com.cmasproject.cmastestserver.constants.ApplicationConstants;
import com.cmasproject.cmastestserver.constants.TestConstants;
import com.cmasproject.cmastestserver.mapper.UserMapper;
import com.cmasproject.cmastestserver.model.LogInRequestDTO;
import com.cmasproject.cmastestserver.model.SignUpRequestDTO;
import com.cmasproject.cmastestserver.repository.PatientRepository;
import com.cmasproject.cmastestserver.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("functional-testing")
public class AuthControllerIT {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:9");

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testSuccessfulUserRegistration() throws Exception {
        SignUpRequestDTO user = SignUpRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1234567890")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();


        mockMvc.perform(post(TestConstants.SIGN_UP_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.createdDate").exists());
    }

    @Rollback
    @Transactional
    @Test
    public void testDuplicateUserRegistration() throws Exception {
        SignUpRequestDTO user = SignUpRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1234567890")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();

        mockMvc.perform(post(TestConstants.SIGN_UP_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").exists());;

        mockMvc.perform(post(TestConstants.SIGN_UP_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.username", is("Username already exists.")))
                .andExpect(jsonPath("$.email", is("Email already exists.")))
                .andExpect(jsonPath("$['phone number']", is("Phone number already exists.")));
    }

    @Rollback
    @Transactional
    @Test
    public void testSuccessfulLogin() throws Exception {
        SignUpRequestDTO user = SignUpRequestDTO.builder()
                .username("loginuser")
                .email("login@example.com")
                .password("password123")
                .firstName("Login")
                .lastName("User")
                .phoneNumber("+9876543210")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();


        mockMvc.perform(post(TestConstants.SIGN_UP_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        LogInRequestDTO loginRequest = LogInRequestDTO.builder()
                .username("loginuser")
                .password("password123")
                .build();

        mockMvc.perform(post(TestConstants.LOG_IN_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(ApplicationConstants.JWT_HEADER));
    }

    @Test
    public void testInvalidUsernameLogin() throws Exception {
        LogInRequestDTO loginRequest = LogInRequestDTO.builder()
                .username("user")
                .password("validpassword")
                .build();

        mockMvc.perform(post(TestConstants.LOG_IN_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("User not found with username: user")));
    }

    @Rollback
    @Transactional
    @Test
    public void testValidUsernameInvalidPasswordLogin() throws Exception {
        SignUpRequestDTO user = SignUpRequestDTO.builder()
                .username("validuser")
                .email("valid@example.com")
                .password("correctpassword")
                .firstName("Valid")
                .lastName("User")
                .phoneNumber("+1122334455")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();

        mockMvc.perform(post(TestConstants.SIGN_UP_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        LogInRequestDTO loginRequest = LogInRequestDTO.builder()
                .username("validuser")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post(TestConstants.LOG_IN_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid password!")));
    }
}
