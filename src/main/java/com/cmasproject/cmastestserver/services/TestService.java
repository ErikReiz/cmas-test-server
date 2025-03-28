package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.TestRecord;
import com.cmasproject.cmastestserver.model.CreateTestRequestDTO;

public interface TestService {
    Boolean isPatientExists(CreateTestRequestDTO request);

    TestRecord createTest(String doctorUsername, String patientUsername);
}
