package com.cmasproject.cmastestserver.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class PatientResponseDTO {
    UUID patientId;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
}
