package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    List<Answer> findByTestRecord_Id(UUID testRecordId);
}
