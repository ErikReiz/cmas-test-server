package com.cmasproject.cmastestserver.model.registration;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class SignUpDoctorResponseDTO extends SignUpResponseDTO {
    private String specialty;
    private String licenseNumber;
}
