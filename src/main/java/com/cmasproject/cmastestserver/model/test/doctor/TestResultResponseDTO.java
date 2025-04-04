package com.cmasproject.cmastestserver.model.test.doctor;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TestResultResponseDTO {
    UUID testId;
    LocalDateTime completedDate;
    Integer totalCmasScore;
}
