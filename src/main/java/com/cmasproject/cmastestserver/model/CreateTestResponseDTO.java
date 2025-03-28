package com.cmasproject.cmastestserver.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTestResponseDTO {
    private String message;
    private String authorUsername;
    private String patientUsername;
}
