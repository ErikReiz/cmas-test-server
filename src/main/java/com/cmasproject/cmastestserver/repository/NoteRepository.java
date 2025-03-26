package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {
}
