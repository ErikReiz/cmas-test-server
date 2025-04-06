package com.cmasproject.cmastestserver.controller.doctor;

import com.cmasproject.cmastestserver.constants.TestConstants;
import com.cmasproject.cmastestserver.model.AssignPatientsRequestDTO;
import com.cmasproject.cmastestserver.model.AssignedPatientsResponseDTO;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;
import com.cmasproject.cmastestserver.services.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
@AutoConfigureMockMvc(addFilters = false)
class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DoctorService doctorService;

    private Authentication mockAuthentication;
    private List<PatientResponseDTO> patientList;
    private Set<PatientResponseDTO> patientSet;

    @BeforeEach
    void setUp() {
        mockAuthentication = mock(Authentication.class);
        given(mockAuthentication.getName()).willReturn("doctor123");

        PatientResponseDTO patient1 = PatientResponseDTO.builder()
                .patientId(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .build();

        PatientResponseDTO patient2 = PatientResponseDTO.builder()
                .patientId(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Smith")
                .build();

        patientList = Arrays.asList(patient1, patient2);
        patientSet = new HashSet<>(patientList);
    }

    @Test
    void testGetAllPatients() throws Exception {
        given(doctorService.getAllPatients()).willReturn(patientList);

        mockMvc.perform(get(TestConstants.GET_ALL_PATIENTS_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(patientList)));

        verify(doctorService).getAllPatients();
    }

    @Test
    void testAssignPatientsToDoctor() throws Exception {
        List<UUID> patientIds = Arrays.asList(
                patientList.get(0).getPatientId(),
                patientList.get(1).getPatientId()
        );

        AssignPatientsRequestDTO requestDTO = AssignPatientsRequestDTO.builder()
                .patientIds(patientIds)
                .build();

        AssignedPatientsResponseDTO expectedResponse = AssignedPatientsResponseDTO.builder()
                .message("Patients assigned successfully.")
                .assignedPatients(patientList)
                .build();

        given(doctorService.assignPatients(any(), anyString())).willReturn(patientList);

        mockMvc.perform(post(TestConstants.ASSIGN_PATIENTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(doctorService).assignPatients(patientIds, "doctor123");
    }

    @Test
    void testGetAssignedPatients() throws Exception {
        given(doctorService.getAssignedPatients(anyString())).willReturn(patientSet);

        mockMvc.perform(get(TestConstants.GET_ASSIGNED_PATIENTS_URL)
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(patientSet)));

        verify(doctorService).getAssignedPatients("doctor123");
    }

    @Test
    void testAssignPatientsEmptyList() throws Exception {
        AssignPatientsRequestDTO requestDTO = AssignPatientsRequestDTO.builder()
                .patientIds(Collections.emptyList())
                .build();

        List<PatientResponseDTO> emptyResponseList = Collections.emptyList();
        AssignedPatientsResponseDTO expectedResponse = AssignedPatientsResponseDTO.builder()
                .message("Patients assigned successfully.")
                .assignedPatients(emptyResponseList)
                .build();

        given(doctorService.assignPatients(any(), anyString())).willReturn(emptyResponseList);

        mockMvc.perform(post(TestConstants.ASSIGN_PATIENTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(doctorService).assignPatients(Collections.emptyList(), "doctor123");
    }

    @Test
    void testGetEmptyAssignedPatients() throws Exception {
        Set<PatientResponseDTO> emptySet = Collections.emptySet();
        given(doctorService.getAssignedPatients(anyString())).willReturn(emptySet);

        mockMvc.perform(get(TestConstants.GET_ASSIGNED_PATIENTS_URL)
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptySet)));

        verify(doctorService).getAssignedPatients("doctor123");
    }
}