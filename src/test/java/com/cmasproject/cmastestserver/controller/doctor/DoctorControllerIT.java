package com.cmasproject.cmastestserver.controller.doctor;

import com.cmasproject.cmastestserver.constants.ApplicationConstants;
import com.cmasproject.cmastestserver.constants.TestConstants;
import com.cmasproject.cmastestserver.model.AssignPatientsRequestDTO;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;
import com.cmasproject.cmastestserver.model.registration.LogInRequestDTO;
import com.cmasproject.cmastestserver.model.registration.SignUpDoctorRequestDTO;
import com.cmasproject.cmastestserver.model.registration.SignUpPatientRequestDTO;
import com.cmasproject.cmastestserver.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("integration-testing")
public class DoctorControllerIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:9");

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthService authService;

    private String doctorToken;
    private String doctorUsername;
    private List<UUID> patientIds;

    @BeforeEach
    @Rollback
    @Transactional
    public void setUp() throws Exception {
        SignUpDoctorRequestDTO doctorUser = SignUpDoctorRequestDTO.builder()
                .username("doctortest")
                .email("doctortest@example.com")
                .password("password123")
                .firstName("Doctor")
                .lastName("Test")
                .phoneNumber("+1231231235")
                .licenseNumber("654321")
                .specialty("Neurology")
                .build();

        authService.registerDoctor(doctorUser);
        doctorUsername = doctorUser.getUsername();

        SignUpPatientRequestDTO patient1 = SignUpPatientRequestDTO.builder()
                .username("patient1")
                .email("patient1@example.com")
                .password("password123")
                .firstName("Patient")
                .lastName("One")
                .phoneNumber("+9876543211")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();

        SignUpPatientRequestDTO patient2 = SignUpPatientRequestDTO.builder()
                .username("patient2")
                .email("patient2@example.com")
                .password("password123")
                .firstName("Patient")
                .lastName("Two")
                .phoneNumber("+9876543212")
                .dateOfBirth(LocalDate.parse("1992-02-02"))
                .build();

        SignUpPatientRequestDTO patient3 = SignUpPatientRequestDTO.builder()
                .username("patient3")
                .email("patient3@example.com")
                .password("password123")
                .firstName("Patient")
                .lastName("Three")
                .phoneNumber("+9876543213")
                .dateOfBirth(LocalDate.parse("1993-03-03"))
                .build();

        authService.registerPatient(patient1);
        authService.registerPatient(patient2);
        authService.registerPatient(patient3);

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

        MvcResult patientsResult = mockMvc.perform(get("/api/doctor/patients")
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .header(ApplicationConstants.JWT_HEADER, doctorToken))
                .andExpect(status().isOk())
                .andReturn();

        List<PatientResponseDTO> patients = objectMapper.readValue(
                patientsResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, PatientResponseDTO.class)
        );

        patientIds = patients.stream()
                .map(PatientResponseDTO::getPatientId)
                .toList();
    }

    @Test
    @Rollback
    @Transactional
    public void testGetAllPatients() throws Exception {
        mockMvc.perform(get("/api/doctor/patients")
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .header(ApplicationConstants.JWT_HEADER, doctorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].firstName").exists());
    }

    @Test
    @Rollback
    @Transactional
    public void testAssignPatientsToDoctor() throws Exception {
        List<UUID> patientsToAssign = new ArrayList<>();
        patientsToAssign.add(patientIds.get(0));
        patientsToAssign.add(patientIds.get(1));

        AssignPatientsRequestDTO requestDTO = AssignPatientsRequestDTO.builder()
                .patientIds(patientsToAssign)
                .build();

        mockMvc.perform(post("/api/doctor/assignPatients")
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .header(ApplicationConstants.JWT_HEADER, doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Patients assigned successfully."))
                .andExpect(jsonPath("$.assignedPatients").isArray())
                .andExpect(jsonPath("$.assignedPatients", hasSize(2)));

        mockMvc.perform(get("/api/doctor/assignedPatients")
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .header(ApplicationConstants.JWT_HEADER, doctorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @Rollback
    @Transactional
    public void testAssignNoPatients() throws Exception {
        // Назначаем пустой список пациентов
        AssignPatientsRequestDTO requestDTO = AssignPatientsRequestDTO.builder()
                .patientIds(List.of())
                .build();

        mockMvc.perform(post("/api/doctor/assignPatients")
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .header(ApplicationConstants.JWT_HEADER, doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Patients assigned successfully."))
                .andExpect(jsonPath("$.assignedPatients").isArray())
                .andExpect(jsonPath("$.assignedPatients", hasSize(0)));
    }

    @Test
    @Rollback
    @Transactional
    public void testGetAssignedPatientsWhenNoneAssigned() throws Exception {
        mockMvc.perform(get("/api/doctor/assignedPatients")
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .header(ApplicationConstants.JWT_HEADER, doctorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Rollback
    @Transactional
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get(TestConstants.GET_ALL_PATIENTS_URL)
                        .with(request -> {
                            request.setScheme("https");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized());
    }
}
