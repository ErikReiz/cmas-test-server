package com.cmasproject.cmastestserver.repository;

import com.azure.core.http.HttpHeaders;
import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Patient getPatientById(UUID patientId);

    Set<Patient> getPatientsByDoctors(Set<Doctor> doctors);

    Patient getPatientByUser(User user);
}
