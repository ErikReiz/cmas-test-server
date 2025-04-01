package com.cmasproject.cmastestserver.controller.test;

import com.cmasproject.cmastestserver.constants.ApplicationConstants;
import com.cmasproject.cmastestserver.constants.TestConstants;
import com.cmasproject.cmastestserver.model.test.doctor.CreateTestRequestDTO;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;
import com.cmasproject.cmastestserver.model.registration.*;
import com.cmasproject.cmastestserver.services.AuthService;
import com.cmasproject.cmastestserver.services.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("integration-testing")
@WithMockUser(roles = "ADMIN")
@Disabled
public class TestCreationControllerIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:9");

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthService authService;

    @Autowired
    DoctorService doctorService;

    private String doctorToken;
    private String doctorUsername;
    private UUID patientId;
    private String patientFirstName;
    private String patientLastName;

    @BeforeEach
    @Rollback
    @Transactional
    public void setUp() throws Exception {
        SignUpDoctorRequestDTO doctorUser = SignUpDoctorRequestDTO.builder()
                .username("testdoctor")
                .email("doctor@example.com")
                .password("password123")
                .firstName("Doctor")
                .lastName("Test")
                .phoneNumber("+1231231234")
                .licenseNumber("123456")
                .specialty("Cardiology")
                .build();

        authService.registerDoctor(doctorUser);

        SignUpPatientRequestDTO patientUser = SignUpPatientRequestDTO.builder()
                .username("patientuser")
                .email("patient@example.com")
                .password("password123")
                .firstName("Patient")
                .lastName("User")
                .phoneNumber("+9876543210")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();

        authService.registerPatient(patientUser);

        List<UUID> patientIds = doctorService.getAllPatients().stream()
                .map(PatientResponseDTO::getPatientId)
                .toList();

        patientId = doctorService.assignPatients(patientIds, doctorUser.getUsername()).get(0).getPatientId();
        patientFirstName = patientUser.getFirstName();
        patientLastName = patientUser.getLastName();

        doctorUsername = doctorUser.getUsername();

        LogInRequestDTO loginRequest = LogInRequestDTO.builder()
                .username(doctorUsername)
                .password(doctorUser.getPassword())
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
                .patientId(patientId)
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
                .andExpect(jsonPath("$.authorUsername", is(doctorUsername)))
                .andExpect(jsonPath("$.patientFirstName", is(patientFirstName)))
                .andExpect(jsonPath("$.patientLastName", is(patientLastName)));
    }

    @Test
    @Rollback
    @Transactional
    public void testInvalidTestCreationWithNonExistentPatient() throws Exception {
        CreateTestRequestDTO createTestRequest = CreateTestRequestDTO.builder()
                .patientId(UUID.randomUUID())
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
                .andExpect(jsonPath("$.error", is("Could not find Patient for ID:" + createTestRequest.getPatientId())));
    }

    @Test
    @Rollback
    @Transactional
    public void testInvalidTestCreationWithEmptyPatientId() throws Exception {
        CreateTestRequestDTO createTestRequest = CreateTestRequestDTO.builder()
                .patientId(null)
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
                .andExpect(jsonPath("$[*].patientId").exists());
    }

    @Test
    @Rollback
    @Transactional
    public void testUnauthorizedTestCreation() throws Exception {
        CreateTestRequestDTO createTestRequest = CreateTestRequestDTO.builder()
                .patientId(patientId)
                .build();

        mockMvc.perform(post(TestConstants.CREATE_TEST_RECORD_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTestRequest)))
                .andExpect(status().isForbidden());
    }
}