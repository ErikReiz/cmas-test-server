package com.cmasproject.cmastestserver.model.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class SignUpDoctorRequestDTO extends SignUpRequestDTO {
    @NotBlank(message = "Speciality is required")
    @Size(max = 100)
    private String specialty;

    @NotBlank(message = "License number is required")
    @Size(max = 50)
    private String licenseNumber;
}
