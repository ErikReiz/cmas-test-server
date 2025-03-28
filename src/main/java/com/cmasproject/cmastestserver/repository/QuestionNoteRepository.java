package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.QuestionNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionNoteRepository extends JpaRepository<QuestionNote, UUID> {
}
