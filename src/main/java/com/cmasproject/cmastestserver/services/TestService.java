package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.model.test.doctor.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TestService {
    CreateTestResponseDTO createTest(String doctorUsername, UUID patientId);
    CreateTestNotesResponseDTO saveTestNotes(CreateTestNotesRequestDTO request);
    List<TestResultResponseDTO> loadPatientTestResults(UUID patientId);
    Set<TestResponseDTO> getTests(String doctorUsername);
    Set<QuestionAnswerDTO> loadTestResults(UUID testId);
}
