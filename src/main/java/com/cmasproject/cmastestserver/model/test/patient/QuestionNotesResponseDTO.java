package com.cmasproject.cmastestserver.model.test.patient;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class QuestionNotesResponseDTO {
    private String message;
    private UUID testId;
    private Map<Integer, String> questionOrderToNotesMap;
}
