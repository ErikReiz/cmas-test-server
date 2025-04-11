package com.cmasproject.cmastestserver.controller.doctor;

import com.cmasproject.cmastestserver.model.test.doctor.*;
import com.cmasproject.cmastestserver.services.TestService;
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
@RequestMapping("/api/doctor/tests")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @PostMapping("/create")
    public ResponseEntity<?> createTest(@Validated @RequestBody CreateTestRequestDTO request, Authentication authentication) {
        String doctorUsername = authentication.getName();

        CreateTestResponseDTO response = testService.createTest(doctorUsername, request.getPatientId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/createNotes")
    public ResponseEntity<?> saveTestNotes(@Validated @RequestBody CreateTestNotesRequestDTO request) {
        CreateTestNotesResponseDTO response = testService.saveTestNotes(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/results/{patientId}")
    public ResponseEntity<?> getTestResults(@PathVariable UUID patientId)
    {
        List<TestResultResponseDTO> response = testService.loadPatientTestResults(patientId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getTests(Authentication authentication) {
        String doctorUsername = authentication.getName();

        Set<TestResponseDTO> response = testService.getTests(doctorUsername);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{testId}")
    public ResponseEntity<?> getFullTestResults(@PathVariable UUID testId) {
        Set<QuestionAnswerDTO> response = testService.loadTestResults(testId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
