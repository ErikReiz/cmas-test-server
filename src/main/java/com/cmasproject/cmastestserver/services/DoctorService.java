package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DoctorService {
    List<PatientResponseDTO> getAllPatients();
    List<PatientResponseDTO> assignPatients(List<UUID> patientIds, String doctorUsername);
}
