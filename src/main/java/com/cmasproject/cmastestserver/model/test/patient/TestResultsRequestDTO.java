package com.cmasproject.cmastestserver.model.test.patient;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class TestResultsRequestDTO {
    @NotNull
    private UUID testId;

    @NotNull
    private List<QuestionAnswerRequestDTO> answers;
}
