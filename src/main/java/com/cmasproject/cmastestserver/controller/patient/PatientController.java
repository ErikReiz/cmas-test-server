package com.cmasproject.cmastestserver.controller.patient;

import com.cmasproject.cmastestserver.model.test.patient.AssignedTestResponseDTO;
import com.cmasproject.cmastestserver.model.test.patient.TestResultsRequestDTO;
import com.cmasproject.cmastestserver.services.PatientService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/tests")
    private ResponseEntity<?> getAssignedTests(Authentication authentication)
    {
        String patientUsername = authentication.getName();
        List<AssignedTestResponseDTO> assignedTests = patientService.getAssignedTests(patientUsername);

        return ResponseEntity.ok().body(assignedTests);
    }

    @PostMapping("/upload")
    private ResponseEntity<?> sendTestResults(@Validated @RequestBody TestResultsRequestDTO testResults)
    {
        if(!patientService.isTestExists(testResults))
            throw new EntityNotFoundException("Could not find Test entity for ID: " + testResults.getTestId());

        patientService.passTestResults(testResults);
        return ResponseEntity.ok("Test results uploaded successfully.");
    }
}
