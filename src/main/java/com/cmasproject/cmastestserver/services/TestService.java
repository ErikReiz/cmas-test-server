package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.model.test.doctor.CreateTestResponseDTO;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;

import java.util.Set;
import java.util.UUID;

public interface TestService {
    Boolean isPatientExists(UUID patientId);

    CreateTestResponseDTO createTest(String doctorUsername, UUID patientId);
    Set<PatientResponseDTO> getPatients(String doctorUsername);
}
