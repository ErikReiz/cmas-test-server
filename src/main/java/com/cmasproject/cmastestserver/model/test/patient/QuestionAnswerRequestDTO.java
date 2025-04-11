package com.cmasproject.cmastestserver.model.test.patient;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class QuestionAnswerRequestDTO {
    @NotNull
    private UUID questionId;

    @NotNull
    private Integer score;
}
