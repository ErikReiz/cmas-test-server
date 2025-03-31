package com.cmasproject.cmastestserver.model.test.doctor;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class QuestionNoteRequestDTO {
    @NotNull
    private UUID questionId;

    @NotBlank
    @Size(max=500)
    private String note;
}
