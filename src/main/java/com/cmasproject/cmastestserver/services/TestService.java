package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.TestRecord;
import com.cmasproject.cmastestserver.model.CreateTestRequestDTO;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;

import java.util.Set;
import java.util.UUID;

public interface TestService {
    Boolean isPatientExists(UUID patientId);

    TestRecord createTest(String doctorUsername, UUID patientId);
    Set<PatientResponseDTO> getPatients(String doctorUsername);
}
