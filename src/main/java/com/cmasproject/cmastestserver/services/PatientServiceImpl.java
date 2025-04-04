package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.*;
import com.cmasproject.cmastestserver.model.test.patient.AssignedTestResponseDTO;
import com.cmasproject.cmastestserver.model.test.patient.QuestionAnswerRequestDTO;
import com.cmasproject.cmastestserver.model.test.patient.QuestionNotesResponseDTO;
import com.cmasproject.cmastestserver.model.test.patient.TestResultsRequestDTO;
import com.cmasproject.cmastestserver.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final TestRecordRepository testRecordRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final QuestionNoteRepository questionNoteRepository;


    @Override
    public Boolean isTestExists(UUID testId)
    {
        return testRecordRepository.existsById(testId);
    }

    @Override
    public void passTestResults(TestResultsRequestDTO testResults)
    {
        UUID testAssignmentId = testResults.getTestId();
        List<QuestionAnswerRequestDTO> answerDtos = testResults.getAnswers();

        TestRecord testAssignment = testRecordRepository.getTestRecordById(testAssignmentId);

        Set<UUID> questionIds = answerDtos.stream()
                .map(QuestionAnswerRequestDTO::getQuestionId)
                .collect(Collectors.toSet());

        Map<UUID, Question> questionsMap = questionRepository.findAllById(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, question -> question));

        if (questionsMap.size() != questionIds.size()) {
            Set<UUID> foundIds = questionsMap.keySet();
            questionIds.removeAll(foundIds);
            throw new EntityNotFoundException("Could not find Question for IDs: " + questionIds);
        }

        AtomicInteger totalScore = new AtomicInteger();

        List<Answer> answersToSave = answerDtos.stream()
                .map(dto -> {
                    Question question = questionsMap.get(dto.getQuestionId());

                    totalScore.addAndGet(dto.getScore());

                    return Answer.builder()
                            .testRecord(testAssignment)
                            .question(question)
                            .score(dto.getScore())
                            .build();
                })
                .collect(Collectors.toList());

        answerRepository.saveAll(answersToSave);

        testAssignment.setCompletedDate(LocalDateTime.now());
        testAssignment.setTotalCmasScore(totalScore.get());
        testRecordRepository.save(testAssignment);
    }

    @Override
    public QuestionNotesResponseDTO loadTestData(UUID testId)
    {
        if(testRecordRepository.existsById(testId))
            throw new EntityNotFoundException("Could not find Test entity for ID: " + testId);

        TestRecord testRecord = testRecordRepository.getTestRecordById(testId);
        Map<Integer, String> questionOrderToNotesMap = questionNoteRepository.getQuestionNotesByTestRecord(testRecord).stream()
                .collect(Collectors.toMap(
                        questionNote -> questionNote.getQuestion().getQuestionNumber(),
                        QuestionNote::getNote
                ));

        return QuestionNotesResponseDTO.builder()
                .message("Test data loaded successfully.")
                .testId(testRecord.getId())
                .questionOrderToNotesMap(questionOrderToNotesMap)
                .build();
    }

    @Override
    public List<AssignedTestResponseDTO> getAssignedTests(String patientUsername)
    {
        User patientUser = userRepository.getUserByUsername(patientUsername);
        Patient patient = patientRepository.getPatientByUser(patientUser);
        return testRecordRepository.getTestRecordsByPatient(patient).stream()
                .map(testRecord -> {
                    User doctorUser = testRecord.getDoctor().getUser();
                    AssignedTestResponseDTO assignedTest = AssignedTestResponseDTO.builder()
                            .testId(testRecord.getId())
                            .doctorFirstName(doctorUser.getFirstName())
                            .doctorLastName(doctorUser.getLastName())
                            .assignedDate(testRecord.getAssignedDate())
                            .testStatus(testRecord.getStatus())
                            .build();

                    return assignedTest;
                })
                .collect(Collectors.toList());
    }
}
