package com.cmasproject.cmastestserver.model.test.doctor;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class QuestionAnswerDTO {
    private UUID questionId;
    private Integer questionNumber;
    private Integer score;
    private String videoLink;
}
