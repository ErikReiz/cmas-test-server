package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.TestRecord;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.entities.enums.Role;
import com.cmasproject.cmastestserver.entities.enums.TestStatus;
import com.cmasproject.cmastestserver.model.CreateTestRequestDTO;
import com.cmasproject.cmastestserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final TestRecordRepository testRecordRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public Boolean isPatientExists(CreateTestRequestDTO request)
    {
        String username = request.getPatientUsername();

        if(userRepository.existsByUsername(username))
        {
            return userRepository.getUserByUsername(username).getRole() == Role.PATIENT;
        }

        return false;
    }

    @Override
    public TestRecord createTest(String doctorUsername, String patientUsername)
    {
        User doctorUser = userRepository.getUserByUsername(doctorUsername);

        Doctor doctor = doctorRepository.getDoctorByUser((doctorUser));

        User patientUser = userRepository.getUserByUsername(patientUsername);
        Patient patient = patientRepository.getPatientByUser((patientUser));

        TestRecord test = TestRecord.builder()
                .doctor(doctor)
                .patient(patient)
                .status(TestStatus.ASSIGNED)
                .build();

        return testRecordRepository.save(test);
    }
}
