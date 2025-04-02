package com.cmasproject.cmastestserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class AssignedPatientsResponseDTO {
    private String message;
    private List<PatientResponseDTO> assignedPatients;
}
