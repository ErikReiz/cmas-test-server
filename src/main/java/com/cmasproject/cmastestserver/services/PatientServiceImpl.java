package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.*;
import com.cmasproject.cmastestserver.model.test.patient.*;
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

        TestRecord testAssignment = testRecordRepository.findTestRecordById(testAssignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Test entity for ID: " + testAssignmentId));

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
        TestRecord testRecord = testRecordRepository.findTestRecordById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Test entity for ID: " + testId));

        List<NoteResponseDTO> questionOrderToNotesMap = questionNoteRepository.findQuestionNotesByTestRecord(testRecord).stream()
                .map(obj -> NoteResponseDTO.builder()
                        .questionOrder(obj.getQuestion().getQuestionNumber())
                        .questionId(obj.getQuestion().getId())
                        .note(obj.getNote())
                        .build())
                .collect(Collectors.toList());

        return QuestionNotesResponseDTO.builder()
                .message("Test data loaded successfully.")
                .testId(testRecord.getId())
                .questionOrderToNotesMap(questionOrderToNotesMap)
                .build();
    }

    @Override
    public List<AssignedTestResponseDTO> getAssignedTests(String patientUsername)
    {
        Patient patient = patientRepository.findByUser_Username(patientUsername)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Patient entity for username: " + patientUsername));

        return testRecordRepository.findTestRecordsByPatient(patient).stream()
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
