package com.cmasproject.cmastestserver.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateTestRequestDTO {
    @NotBlank(message = "Patient username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String patientUsername;
}
