package com.cmasproject.cmastestserver.repository;

import com.azure.core.http.HttpHeaders;
import com.cmasproject.cmastestserver.entities.Question;
import com.cmasproject.cmastestserver.entities.TestRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    Optional<Question> findQuestionById(UUID id);

    List<Question> findQuestionByTestRecord(TestRecord testRecord);
}
