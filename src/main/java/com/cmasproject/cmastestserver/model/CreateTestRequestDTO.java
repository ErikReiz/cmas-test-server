package com.cmasproject.cmastestserver.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class CreateTestRequestDTO {
    @NotBlank(message = "Patient id is required")
    @Size(min = 36, max = 36, message = "ID must be 36 characters")
    private UUID patientId;
}
