package com.cmasproject.cmastestserver.controller.doctor;

import com.cmasproject.cmastestserver.entities.TestRecord;
import com.cmasproject.cmastestserver.exceptions.TestCreationException;
import com.cmasproject.cmastestserver.model.CreateTestRequestDTO;
import com.cmasproject.cmastestserver.model.CreateTestResponseDTO;
import com.cmasproject.cmastestserver.services.TestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class TestCreationController {
    private final TestService testService;
    private final ObjectMapper objectMapper;

    @PostMapping("/test/create")
    public ResponseEntity<?> createTest(@Validated @RequestBody CreateTestRequestDTO createTestRequest, Authentication authentication) {
        if(!testService.isPatientExists(createTestRequest))
            throw new TestCreationException("Patient does not exist.");

        String doctorUsername = authentication.getName();
        String patientUsername = createTestRequest.getPatientUsername();

        TestRecord createdTest = testService.createTest(doctorUsername, patientUsername);

        CreateTestResponseDTO response = CreateTestResponseDTO.builder()
                .message("Test created successfully.")
                .authorUsername(doctorUsername)
                .patientUsername(patientUsername)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
