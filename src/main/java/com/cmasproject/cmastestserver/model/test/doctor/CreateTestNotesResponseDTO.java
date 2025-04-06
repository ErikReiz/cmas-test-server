package com.cmasproject.cmastestserver.model.test.doctor;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class CreateTestNotesResponseDTO {
    private String message;
    private UUID testId;
    private Set<UUID> savedNotesIds;
}
