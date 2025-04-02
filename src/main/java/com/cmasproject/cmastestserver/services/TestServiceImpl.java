package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.*;
import com.cmasproject.cmastestserver.entities.enums.TestStatus;
import com.cmasproject.cmastestserver.mapper.PatientMapper;
import com.cmasproject.cmastestserver.model.test.doctor.CreateTestNotesResponseDTO;
import com.cmasproject.cmastestserver.model.test.doctor.CreateTestResponseDTO;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;
import com.cmasproject.cmastestserver.model.test.doctor.CreateTestNotesRequestDTO;
import com.cmasproject.cmastestserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService, PatientMapper {
    private final TestRecordRepository testRecordRepository;
    private final QuestionRepository questionRepository;
    private final QuestionNoteRepository questionNoteRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public Boolean isPatientExists(UUID patientId)
    {
        return patientRepository.existsById(patientId);
    }

    @Override
    public Boolean isTestExists(UUID testId)
    {
        return testRecordRepository.existsById(testId);
    }

    @Override
    public CreateTestResponseDTO createTest(String doctorUsername, UUID patientId)
    {
        final int numberOfQuestions = 15;

        User doctorUser = userRepository.getUserByUsername(doctorUsername);
        Doctor doctor = doctorRepository.getDoctorByUser((doctorUser));

        Patient patient = patientRepository.getPatientById(patientId);

        TestRecord savedTest = saveTestRecord(doctor, patient);

        List<Question> savedQuestions = saveQuestions(numberOfQuestions, savedTest);

        Map<Integer, UUID> questionOrderToIdMap = IntStream.range(0, numberOfQuestions)
                .boxed()
                .collect(Collectors.toMap(
                        i -> i,
                        i -> questionRepository.save(savedQuestions.get(i)).getId()
                ));

        User patientUser = savedTest.getPatient().getUser();

        return CreateTestResponseDTO.builder()
                .message("Test created successfully.")
                .authorUsername(doctorUsername)
                .patientFirstName(patientUser.getFirstName())
                .patientLastName(patientUser.getLastName())
                .questionOrderToIdMap(questionOrderToIdMap)
                .build();
    }

    @Override
    public CreateTestNotesResponseDTO saveTestNotes(CreateTestNotesRequestDTO request)
    {
        TestRecord testRecord = testRecordRepository.getTestRecordById(request.getTestId());

        Set<QuestionNote> questionNotes =request.getNotes().stream()
                .map(note -> QuestionNote.builder()
                        .testRecord(testRecord)
                        .question(questionRepository.getQuestionsById((note.getQuestionId())))
                        .note(note.getNote())
                        .build())
                .collect(Collectors.toSet());

        Set<UUID> savedNoteIds = questionNoteRepository.saveAll(questionNotes).stream()
                .map(QuestionNote::getId)
                .collect(Collectors.toSet());

        return CreateTestNotesResponseDTO.builder()
                .message("Test notes saved successfully.")
                .savedNotesIds(savedNoteIds)
                .build();
    }

    private static List<Question> saveQuestions(int numberOfQuestions, TestRecord savedTest)
    {
        List<Question> questions = IntStream.range(0, numberOfQuestions)
                .mapToObj(i -> Question.builder()
                        .testRecord(savedTest)
                        .build())
                .collect(Collectors.toList());

        return questions;
    }

    private TestRecord saveTestRecord(Doctor doctor, Patient patient)
    {
        TestRecord test = TestRecord.builder()
                .doctor(doctor)
                .patient(patient)
                .status(TestStatus.ASSIGNED)
                .build();

        TestRecord savedTest = testRecordRepository.save(test);
        return savedTest;
    }

    public Set<PatientResponseDTO> getPatients(String doctorUsername)
    {
        User user = userRepository.getUserByUsername(doctorUsername);
        return patientRepository.getPatientsByDoctors(Set.of(doctorRepository.findDoctorByUser(user)))
                .stream().map(this::mapToPatientResponseDTO)
                .collect(Collectors.toSet());
    }
}