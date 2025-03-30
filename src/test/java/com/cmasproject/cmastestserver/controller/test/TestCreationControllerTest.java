package com.cmasproject.cmastestserver.controller.test;

import com.cmasproject.cmastestserver.constants.TestConstants;
import com.cmasproject.cmastestserver.controller.doctor.TestCreationController;
import com.cmasproject.cmastestserver.entities.TestRecord;
import com.cmasproject.cmastestserver.model.CreateTestRequestDTO;
import com.cmasproject.cmastestserver.model.CreateTestResponseDTO;
import com.cmasproject.cmastestserver.services.TestService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(TestCreationController.class)
@AutoConfigureMockMvc(addFilters = false)
class TestCreationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TestService testService;

    private CreateTestRequestDTO validRequest;
    private TestRecord mockTest;
    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        validRequest = CreateTestRequestDTO.builder()
                .patientUsername("patient123")
                .build();

        UUID testId = UUID.randomUUID();
        mockTest = TestRecord.builder()
                .id(testId)
                .build();

        mockAuthentication = mock(Authentication.class);
        given(mockAuthentication.getName()).willReturn("doctor123");
    }

    @Test
    void testPatientDoesNotExist() throws Exception {
        given(testService.isPatientExists(any(CreateTestRequestDTO.class))).willReturn(false);

        mockMvc.perform(post(TestConstants.CREATE_TEST_RECORD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .principal(mockAuthentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Patient does not exist."));

        verify(testService).isPatientExists(any(CreateTestRequestDTO.class));
        verify(testService, never()).createTest(anyString(), anyString());
    }

    @Test
    void testSuccessfulTestCreation() throws Exception {
        given(testService.isPatientExists(any(CreateTestRequestDTO.class))).willReturn(true);
        given(testService.createTest(anyString(), anyString())).willReturn(mockTest);
        given(testService.createTest(mockAuthentication.getName(), validRequest.getPatientUsername())).willReturn(mockTest);

        CreateTestResponseDTO expectedResponse = CreateTestResponseDTO.builder()
                .message("Test created successfully.")
                .authorUsername(mockAuthentication.getName())
                .patientFirstName(validRequest.getPatientUsername())
                .patientLastName(va)
                .build();

        mockMvc.perform(post(TestConstants.CREATE_TEST_RECORD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .principal(mockAuthentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(testService).isPatientExists(any(CreateTestRequestDTO.class));
        verify(testService).createTest(mockAuthentication.getName(), validRequest.getPatientUsername());
    }

    @Test
    void testInvalidRequest() throws Exception {
        CreateTestRequestDTO invalidRequest = CreateTestRequestDTO.builder()
                .patientUsername("")
                .build();

        mockMvc.perform(post(TestConstants.CREATE_TEST_RECORD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .principal(mockAuthentication))
                .andExpect(status().isBadRequest());

        verify(testService, never()).isPatientExists(any(CreateTestRequestDTO.class));
        verify(testService, never()).createTest(anyString(), anyString());
    }
}
