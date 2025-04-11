package com.cmasproject.cmastestserver.mapper;

import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;

public interface PatientMapper {
    default PatientResponseDTO mapToPatientResponseDTO(Patient patient) {
        return PatientResponseDTO.builder()
                .patientId(patient.getUser().getId())
                .firstName(patient.getUser().getFirstName())
                .lastName(patient.getUser().getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .build();
    }
}