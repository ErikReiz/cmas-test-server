package com.cmasproject.cmastestserver.model.test.doctor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTestResponseDTO {
    private String message;
    private String authorUsername;
    private String patientFirstName;
    private String patientLastName;
}
