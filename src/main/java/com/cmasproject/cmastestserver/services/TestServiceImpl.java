package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.*;
import com.cmasproject.cmastestserver.entities.enums.TestStatus;
import com.cmasproject.cmastestserver.mapper.PatientMapper;
import com.cmasproject.cmastestserver.model.test.doctor.*;
import com.cmasproject.cmastestserver.repository.*;
import jakarta.persistence.EntityNotFoundException;
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
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AnswerRepository answerRepository;

    @Override
    public CreateTestResponseDTO createTest(String doctorUsername, UUID patientId)
    {
        final int numberOfQuestions = 14;

        Doctor doctor = doctorRepository.findByUser_Username(doctorUsername)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Doctor entity for username: " + doctorUsername));

        Patient patient = patientRepository.findPatientById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Patient entity for ID: " + patientId));

        TestRecord savedTest = saveTestRecord(doctor, patient);

        List<Question> savedQuestions = saveQuestions(numberOfQuestions, savedTest);

        Map<Integer, UUID> questionOrderToIdMap = new HashMap<>();

        for(int i = 0; i < savedQuestions.size(); i++) {
            questionOrderToIdMap.put(i, savedQuestions.get(i).getId());
        }

        User patientUser = savedTest.getPatient().getUser();

        return CreateTestResponseDTO.builder()
                .message("Test created successfully.")
                .authorUsername(doctorUsername)
                .patientFirstName(patientUser.getFirstName())
                .patientLastName(patientUser.getLastName())
                .testId(savedTest.getId())
                .questionOrderToIdMap(questionOrderToIdMap)
                .build();
    }

    @Override
    public CreateTestNotesResponseDTO saveTestNotes(CreateTestNotesRequestDTO request)
    {
        TestRecord testRecord = testRecordRepository.findTestRecordById(request.getTestId())
                .orElseThrow(() -> new EntityNotFoundException("Could not find Test entity for ID: " + request.getTestId()));

        Set<QuestionNote> questionNotes =request.getNotes().stream()
                .map(note ->  {
                    Question question = questionRepository.findQuestionById(note.getQuestionId())
                            .orElseThrow(() -> new EntityNotFoundException("Could not find Question entity for ID: " + note.getQuestionId()));

                    return QuestionNote.builder()
                            .testRecord(testRecord)
                            .question(question)
                            .note(note.getNote())
                            .build();
                })
                .collect(Collectors.toSet());

        Set<UUID> savedNoteIds = questionNoteRepository.saveAll(questionNotes).stream()
                .map(QuestionNote::getId)
                .collect(Collectors.toSet());

        return CreateTestNotesResponseDTO.builder()
                .message("Test notes saved successfully.")
                .testId(testRecord.getId())
                .savedNotesIds(savedNoteIds)
                .build();
    }

    @Override
    public List<TestResultResponseDTO> loadPatientTestResults(UUID patientId)
    {
        Patient patient = patientRepository.findPatientById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Patient entity for ID: " + patientId));

        return testRecordRepository.findTestRecordsByPatient(patient).stream()
                 .map(testRecord -> TestResultResponseDTO.builder()
                         .testId(testRecord.getId())
                         .testStatus(testRecord.getStatus())
                         .assignedDate(testRecord.getAssignedDate())
                         .totalCmasScore(testRecord.getTotalCmasScore())
                         .completedDate(testRecord.getCompletedDate())
                         .build()).collect(Collectors.toList());
    }


    @Override
    public Set<TestResponseDTO> getTests(String doctorUsername)
    {
        List<TestRecord> tests = testRecordRepository.findByDoctor_User_Username(doctorUsername);

        return tests.stream()
                .map(testRecord -> TestResponseDTO.builder()
                        .testId(testRecord.getId())
                        .patientId(testRecord.getPatient().getId())
                        .patientFirstName(testRecord.getPatient().getUser().getFirstName())
                        .patientLastName(testRecord.getPatient().getUser().getLastName())
                        .testStatus(testRecord.getStatus())
                        .assignedDate(testRecord.getAssignedDate())
                        .completedDate(testRecord.getCompletedDate())
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<QuestionAnswerDTO> loadTestResults(UUID testId)
    {
        List<Answer> answerList = answerRepository.findByTestRecord_Id(testId);

        if(answerList.isEmpty())
        {
            throw new EntityNotFoundException("Could not find answers for Test ID: " + testId);
        }

        return answerList.stream()
                .map(answer -> QuestionAnswerDTO.builder()
                        .questionId(answer.getQuestion().getId())
                        .questionNumber(answer.getQuestion().getQuestionNumber())
                        .score(answer.getScore())
                        .build())
                .collect(Collectors.toSet());
    }

    private List<Question> saveQuestions(int numberOfQuestions, TestRecord savedTest)
    {
        List<Question> questions = IntStream.range(0, numberOfQuestions)
                .mapToObj(i -> Question.builder()
                        .questionNumber(i)
                        .testRecord(savedTest)
                        .build())
                .collect(Collectors.toList());

        return questionRepository.saveAll(questions);
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
}
