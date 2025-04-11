package com.cmasproject.cmastestserver.model.test.doctor;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class CreateTestNotesRequestDTO {
    @NotNull
    private UUID testId;

    @NotNull
    private Set<QuestionNoteRequestDTO> notes;
}
