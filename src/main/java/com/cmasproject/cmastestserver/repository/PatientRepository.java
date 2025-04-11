package com.cmasproject.cmastestserver.repository;

import com.azure.core.http.HttpHeaders;
import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findPatientById(UUID patientId);

    Set<Patient> findPatientsByDoctors(Set<Doctor> doctors);

    Optional<Patient> findPatientByUser(User user);

    Optional<Patient> findByUser_Username(@NotBlank @Size(min=3, max=50) String userUsername);
}
