package com.cmasproject.cmastestserver.model.registration;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class SignUpPatientRequestDTO extends SignUpRequestDTO {
    @Past(message = "Date of birth should be in the past")
    private LocalDate dateOfBirth;
}
