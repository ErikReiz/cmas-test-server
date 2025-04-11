package com.cmasproject.cmastestserver.model.test.doctor;

import com.cmasproject.cmastestserver.model.test.patient.QuestionAnswerRequestDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TestNotesRequestDTO {
    @NotNull
    private UUID testId;

    @NotNull
    private List<QuestionNoteRequestDTO> notes;
}
