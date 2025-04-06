package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.QuestionNote;
import com.cmasproject.cmastestserver.entities.TestRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionNoteRepository extends JpaRepository<QuestionNote, UUID> {
    List<QuestionNote> findQuestionNotesByTestRecord(TestRecord testRecord);
}
