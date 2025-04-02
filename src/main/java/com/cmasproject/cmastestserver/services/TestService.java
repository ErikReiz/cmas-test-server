package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.model.test.doctor.CreateTestNotesResponseDTO;
import com.cmasproject.cmastestserver.model.test.doctor.CreateTestResponseDTO;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;
import com.cmasproject.cmastestserver.model.test.doctor.CreateTestNotesRequestDTO;

import java.util.Set;
import java.util.UUID;

public interface TestService {
    Boolean isPatientExists(UUID patientId);
    Boolean isTestExists(UUID testId);

    CreateTestResponseDTO createTest(String doctorUsername, UUID patientId);
    CreateTestNotesResponseDTO saveTestNotes(CreateTestNotesRequestDTO request);
    Set<PatientResponseDTO> getPatients(String doctorUsername);
}
