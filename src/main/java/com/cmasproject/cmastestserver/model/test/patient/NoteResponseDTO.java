package com.cmasproject.cmastestserver.model.test.patient;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Builder
@Data
public class NoteResponseDTO {
    private int questionOrder;
    private UUID questionId;
    private String note;
}
