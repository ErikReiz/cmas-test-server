package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.model.test.patient.AssignedTestResponseDTO;
import com.cmasproject.cmastestserver.model.test.patient.QuestionNotesResponseDTO;
import com.cmasproject.cmastestserver.model.test.patient.TestResultsRequestDTO;

import java.util.List;
import java.util.UUID;

public interface PatientService {
    Boolean isTestExists(UUID testId);

    void passTestResults(TestResultsRequestDTO testResults);
    QuestionNotesResponseDTO loadTestData(UUID testId);

    List<AssignedTestResponseDTO> getAssignedTests(String patientUsername);
}
