package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.model.test.patient.AssignedTestResponseDTO;
import com.cmasproject.cmastestserver.model.test.patient.TestResultsRequestDTO;

import java.util.List;

public interface PatientService {
    Boolean isTestExists(TestResultsRequestDTO testResults);

    void passTestResults(TestResultsRequestDTO testResults);

    List<AssignedTestResponseDTO> getAssignedTests(String patientUsername);

}
