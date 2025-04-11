package com.cmasproject.cmastestserver.model.test.doctor;

import com.cmasproject.cmastestserver.entities.enums.TestStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TestResultResponseDTO {
    private UUID testId;
    private TestStatus testStatus;
    private LocalDateTime assignedDate;
    private LocalDateTime completedDate;
    private Integer totalCmasScore;
}
