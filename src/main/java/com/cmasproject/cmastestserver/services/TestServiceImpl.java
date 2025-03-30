package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.TestRecord;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.entities.enums.Role;
import com.cmasproject.cmastestserver.entities.enums.TestStatus;
import com.cmasproject.cmastestserver.mapper.PatientMapper;
import com.cmasproject.cmastestserver.model.CreateTestRequestDTO;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;
import com.cmasproject.cmastestserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService, PatientMapper {
    private final TestRecordRepository testRecordRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public Boolean isPatientExists(UUID patientId)
    {
        return patientRepository.existsById(patientId);
    }

    @Override
    public TestRecord createTest(String doctorUsername, UUID patientId)
    {
        User doctorUser = userRepository.getUserByUsername(doctorUsername);

        Doctor doctor = doctorRepository.getDoctorByUser((doctorUser));

        Patient patient = patientRepository.getPatientById(patientId);

        TestRecord test = TestRecord.builder()
                .doctor(doctor)
                .patient(patient)
                .status(TestStatus.ASSIGNED)
                .build();

        return testRecordRepository.save(test);
    }

    public Set<PatientResponseDTO> getPatients(String doctorUsername)
    {
        User user = userRepository.getUserByUsername(doctorUsername);
        return patientRepository.getPatientsByDoctors(Set.of(doctorRepository.findDoctorByUser(user)))
                .stream().map(this::mapToPatientResponseDTO)
                .collect(Collectors.toSet());
    }
}