package com.cmasproject.cmastestserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AssignPatientsRequestDTO {
    private List<UUID> patientIds;
}
