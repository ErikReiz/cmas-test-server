package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
