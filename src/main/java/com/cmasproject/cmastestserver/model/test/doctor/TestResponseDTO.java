package com.cmasproject.cmastestserver.model.test.doctor;

import com.cmasproject.cmastestserver.entities.enums.TestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TestResponseDTO {
    private UUID testId;
    private UUID patientId;
    private String patientFirstName;
    private String patientLastName;
    private TestStatus testStatus;
    private LocalDateTime completedDate;
    private LocalDateTime assignedDate;
}
