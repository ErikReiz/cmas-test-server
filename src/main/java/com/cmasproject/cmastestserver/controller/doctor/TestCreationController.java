package com.cmasproject.cmastestserver.controller.doctor;

import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.TestRecord;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.exceptions.TestCreationException;
import com.cmasproject.cmastestserver.model.CreateTestRequestDTO;
import com.cmasproject.cmastestserver.model.CreateTestResponseDTO;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;
import com.cmasproject.cmastestserver.services.TestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/doctor/test")
@RequiredArgsConstructor
public class TestCreationController {
    private final TestService testService;
    private final ObjectMapper objectMapper;

    @PostMapping("/create")
    public ResponseEntity<?> createTest(@Validated @RequestBody CreateTestRequestDTO request, Authentication authentication) {
        if(!testService.isPatientExists(request.getPatientId()))
            throw new TestCreationException("Patient does not exist.");

        String doctorUsername = authentication.getName();

        TestRecord testRecord = testService.createTest(doctorUsername, request.getPatientId());
        User patientUser = testRecord.getPatient().getUser();

        CreateTestResponseDTO response = CreateTestResponseDTO.builder()
                .message("Test created successfully.")
                .authorUsername(doctorUsername)
                .patientFirstName(patientUser.getFirstName())
                .patientLastName(patientUser.getFirstName())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/patients")
    public ResponseEntity<?> getPatients(Authentication authentication) {
        String doctorUsername = authentication.getName();

        Set<PatientResponseDTO> response = testService.getPatients(doctorUsername);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
