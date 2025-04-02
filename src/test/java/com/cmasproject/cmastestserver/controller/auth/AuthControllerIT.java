package com.cmasproject.cmastestserver.controller.auth;

import com.cmasproject.cmastestserver.constants.ApplicationConstants;
import com.cmasproject.cmastestserver.constants.TestConstants;
import com.cmasproject.cmastestserver.model.registration.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
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
@ActiveProfiles("integration-testing")
public class AuthControllerIT {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:9");

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Rollback
    @Transactional
    @Test
    public void testSuccessfulPatientRegistration() throws Exception {
        SignUpPatientRequestDTO user = SignUpPatientRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1234567890")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();

        SignUpPatientResponseDTO expectedResponse = SignUpPatientResponseDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .build();

        mockMvc.perform(post(TestConstants.SIGN_UP_PATIENT_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Rollback
    @Transactional
    @WithMockUser(roles = "ADMIN")
    @Test
    public void testSuccessfulDoctorRegistration() throws Exception {
        SignUpDoctorRequestDTO user = SignUpDoctorRequestDTO.builder()
                .username("testdoctor")
                .email("doctor@example.com")
                .password("password123")
                .firstName("Doctor")
                .lastName("Test")
                .phoneNumber("+1231231234")
                .licenseNumber("123456")
                .specialty("Cardiology")
                .build();

        SignUpDoctorResponseDTO expectedResponse = SignUpDoctorResponseDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .licenseNumber(user.getLicenseNumber())
                .specialty(user.getSpecialty())
                .build();

        mockMvc.perform(post(TestConstants.SIGN_UP_DOCTOR_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Rollback
    @Transactional
    @Test
    public void testDuplicateUserRegistration() throws Exception {
        SignUpPatientRequestDTO user = SignUpPatientRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1234567890")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();

        mockMvc.perform(post(TestConstants.SIGN_UP_PATIENT_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").exists());;

        mockMvc.perform(post(TestConstants.SIGN_UP_PATIENT_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.username", is("Username already exists.")))
                .andExpect(jsonPath("$.email", is("Email already exists.")))
                .andExpect(jsonPath("$.phoneNumber", is("Phone number already exists.")));
    }

    @Rollback
    @Transactional
    @Test
    public void testSuccessfulLogin() throws Exception {
        SignUpPatientRequestDTO user = SignUpPatientRequestDTO.builder()
                .username("loginuser")
                .email("login@example.com")
                .password("password123")
                .firstName("Login")
                .lastName("User")
                .phoneNumber("+9876543210")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();


        mockMvc.perform(post(TestConstants.SIGN_UP_PATIENT_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        LogInRequestDTO loginRequest = LogInRequestDTO.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();

        mockMvc.perform(post(TestConstants.LOG_IN_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
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
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("User not found with username: user")));
    }

    @Rollback
    @Transactional
    @Test
    public void testValidUsernameInvalidPasswordLogin() throws Exception {
        SignUpPatientRequestDTO user = SignUpPatientRequestDTO.builder()
                .username("validuser")
                .email("valid@example.com")
                .password("correctpassword")
                .firstName("Valid")
                .lastName("User")
                .phoneNumber("+1122334455")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();

        mockMvc.perform(post(TestConstants.SIGN_UP_PATIENT_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        LogInRequestDTO loginRequest = LogInRequestDTO.builder()
                .username(user.getUsername())
                .password("wrongpassword")
                .build();

        mockMvc.perform(post(TestConstants.LOG_IN_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid password!")));
    }
}
