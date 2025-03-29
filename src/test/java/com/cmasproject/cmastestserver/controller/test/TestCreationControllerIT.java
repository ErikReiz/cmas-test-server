package com.cmasproject.cmastestserver.controller.test;

import com.cmasproject.cmastestserver.constants.ApplicationConstants;
import com.cmasproject.cmastestserver.constants.TestConstants;
import com.cmasproject.cmastestserver.model.CreateTestRequestDTO;
import com.cmasproject.cmastestserver.model.registration.LogInRequestDTO;
import com.cmasproject.cmastestserver.model.registration.SignUpPatientRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("functional-testing")
public class TestCreationControllerIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:9");

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    private String doctorToken;
    private String patientUsername;

    @BeforeEach
    @Rollback
    @Transactional
    public void setUp() throws Exception {
        // Create a doctor user
        SignUpPatientRequestDTO doctorUser = SignUpPatientRequestDTO.builder()
                .username("doctoruser")
                .email("doctor@example.com")
                .password("password123")
                .firstName("Doctor")
                .lastName("User")
                .phoneNumber("+1234567890")
                .dateOfBirth(LocalDate.parse("1980-01-01"))
                .build();

        mockMvc.perform(post(TestConstants.SIGN_UP_PATIENT_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorUser)))
                .andExpect(status().isCreated());


        SignUpPatientRequestDTO patientUser = SignUpPatientRequestDTO.builder()
                .username("patientuser")
                .email("patient@example.com")
                .password("password123")
                .firstName("Patient")
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
                        .content(objectMapper.writeValueAsString(patientUser)))
                .andExpect(status().isCreated());

        patientUsername = patientUser.getUsername();

        // Login as doctor to get token
        LogInRequestDTO loginRequest = LogInRequestDTO.builder()
                .username("doctoruser")
                .password("password123")
                .build();

        MvcResult result = mockMvc.perform(post(TestConstants.LOG_IN_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().exists(ApplicationConstants.JWT_HEADER))
                .andReturn();

        doctorToken = result.getResponse().getHeader(ApplicationConstants.JWT_HEADER);
    }

    @Test
    @Rollback
    @Transactional
    public void testSuccessfulTestCreation() throws Exception {
        CreateTestRequestDTO createTestRequest = CreateTestRequestDTO.builder()
                .patientUsername(patientUsername)
                .build();

        mockMvc.perform(post(TestConstants.CREATE_TEST_RECORD_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .header(ApplicationConstants.JWT_HEADER, doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTestRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Test created successfully.")))
                .andExpect(jsonPath("$.authorUsername", is("doctoruser")))
                .andExpect(jsonPath("$.patientUsername", is(patientUsername)));
    }

    @Test
    @Rollback
    @Transactional
    public void testInvalidTestCreation_NonExistentPatient() throws Exception {
        CreateTestRequestDTO createTestRequest = CreateTestRequestDTO.builder()
                .patientUsername("nonexistentpatient")
                .build();

        mockMvc.perform(post(TestConstants.CREATE_TEST_RECORD_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .header(ApplicationConstants.JWT_HEADER, doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTestRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Patient does not exist.")));
    }

    @Test
    @Rollback
    @Transactional
    public void testInvalidTestCreation_EmptyPatientUsername() throws Exception {
        CreateTestRequestDTO createTestRequest = CreateTestRequestDTO.builder()
                .patientUsername("")
                .build();

        mockMvc.perform(post(TestConstants.CREATE_TEST_RECORD_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .header(ApplicationConstants.JWT_HEADER, doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTestRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].patientUsername").exists());
    }

    @Test
    @Rollback
    @Transactional
    public void testUnauthorizedTestCreation() throws Exception {
        CreateTestRequestDTO createTestRequest = CreateTestRequestDTO.builder()
                .patientUsername(patientUsername)
                .build();

        mockMvc.perform(post(TestConstants.CREATE_TEST_RECORD_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTestRequest)))
                .andExpect(status().isUnauthorized());
    }
}