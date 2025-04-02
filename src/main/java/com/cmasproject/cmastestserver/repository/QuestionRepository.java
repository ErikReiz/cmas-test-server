package com.cmasproject.cmastestserver.repository;

import com.azure.core.http.HttpHeaders;
import com.cmasproject.cmastestserver.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    Question getQuestionsById(UUID id);
}
