package com.cmasproject.cmastestserver.controller.doctor;

import com.cmasproject.cmastestserver.model.test.doctor.*;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;
import com.cmasproject.cmastestserver.services.TestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            throw new EntityNotFoundException("Could not find Patient for ID:" + request.getPatientId());

        String doctorUsername = authentication.getName();

        CreateTestResponseDTO response = testService.createTest(doctorUsername, request.getPatientId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/createNotes")
    public ResponseEntity<?> saveTestNotes(@Validated @RequestBody CreateTestNotesRequestDTO request) {
        if(!testService.isTestExists(request.getTestId()))
            throw new EntityNotFoundException("Could not find Test entity for ID:" + request.getTestId());

        CreateTestNotesResponseDTO response = testService.saveTestNotes(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/results/{patientId}")
    public ResponseEntity<?> getTestResults(@PathVariable UUID patientId)
    {
        if(!testService.isPatientExists(patientId))
            throw new EntityNotFoundException("Could not find Patient for ID:" + patientId);

        List<TestResultResponseDTO> response = testService.loadTestResults(patientId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
