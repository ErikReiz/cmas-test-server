package com.cmasproject.cmastestserver.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateTestResponseDTO {
    private String message;
    private String authorUsername;
    private String patientFirstName;
    private String patientLastName;
}
