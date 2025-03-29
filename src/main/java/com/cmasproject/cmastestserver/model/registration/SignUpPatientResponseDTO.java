package com.cmasproject.cmastestserver.model.registration;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class SignUpPatientResponseDTO extends SignUpResponseDTO {
    private LocalDate dateOfBirth;
}
