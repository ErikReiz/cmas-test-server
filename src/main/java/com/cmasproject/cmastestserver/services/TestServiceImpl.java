package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.*;
import com.cmasproject.cmastestserver.entities.enums.TestStatus;
import com.cmasproject.cmastestserver.mapper.PatientMapper;
import com.cmasproject.cmastestserver.model.test.doctor.CreateTestResponseDTO;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;
import com.cmasproject.cmastestserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService, PatientMapper {
    private final TestRecordRepository testRecordRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public Boolean isPatientExists(UUID patientId)
    {
        return patientRepository.existsById(patientId);
    }

    @Override
    public CreateTestResponseDTO createTest(String doctorUsername, UUID patientId)
    {
        final int numberOfQuestions = 15;

        User doctorUser = userRepository.getUserByUsername(doctorUsername);

        Doctor doctor = doctorRepository.getDoctorByUser((doctorUser));
        Patient patient = patientRepository.getPatientById(patientId);

        TestRecord test = TestRecord.builder()
                .doctor(doctor)
                .patient(patient)
                .status(TestStatus.ASSIGNED)
                .build();

        TestRecord savedTest = testRecordRepository.save(test);

        List<Question> questions = IntStream.range(0, numberOfQuestions)
                .mapToObj(i -> Question.builder()
                        .test(savedTest)
                        .build())
                .collect(Collectors.toList());

        questionRepository.saveAll(questions);

        User patientUser = savedTest.getPatient().getUser();

        return CreateTestResponseDTO.builder()
                .message("Test created successfully.")
                .authorUsername(doctorUsername)
                .patientFirstName(patientUser.getFirstName())
                .patientLastName(patientUser.getLastName())
                .build();
    }

    public Set<PatientResponseDTO> getPatients(String doctorUsername)
    {
        User user = userRepository.getUserByUsername(doctorUsername);
        return patientRepository.getPatientsByDoctors(Set.of(doctorRepository.findDoctorByUser(user)))
                .stream().map(this::mapToPatientResponseDTO)
                .collect(Collectors.toSet());
    }
}