package com.cmasproject.cmastestserver.model.test.patient;

import com.cmasproject.cmastestserver.entities.enums.TestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class AssignedTestResponseDTO {
    private UUID testId;
    private LocalDateTime assignedDate;
    private String doctorFirstName;
    private String doctorLastName;
    private TestStatus testStatus;
}
