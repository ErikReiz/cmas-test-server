package com.cmasproject.cmastestserver.model.test.doctor;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class CreateTestRequestDTO {
    @NotNull
    private UUID patientId;
}
