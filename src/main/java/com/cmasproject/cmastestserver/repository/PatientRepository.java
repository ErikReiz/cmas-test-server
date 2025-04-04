package com.cmasproject.cmastestserver.repository;

import com.azure.core.http.HttpHeaders;
import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findPatientById(UUID patientId);

    Set<Patient> findPatientsByDoctors(Set<Doctor> doctors);

    Optional<Patient> findPatientByUser(User user);
}
