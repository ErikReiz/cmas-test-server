package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
}
