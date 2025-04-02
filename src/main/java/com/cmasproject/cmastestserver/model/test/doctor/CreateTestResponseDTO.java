package com.cmasproject.cmastestserver.model.test.doctor;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class CreateTestResponseDTO {
    private String message;
    private String authorUsername;
    private String patientFirstName;
    private String patientLastName;
    Map<Integer, UUID> questionOrderToIdMap;
}
