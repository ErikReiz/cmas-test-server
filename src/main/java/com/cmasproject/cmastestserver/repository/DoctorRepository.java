package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Set<Doctor> findDoctorsByUser(User userByUsername);

    Optional<Doctor> findDoctorByUser(User user);

    Optional<Doctor> findByUser_Username(@NotBlank @Size(min=3, max=50) String userUsername);

    Boolean existsByLicenseNumber(@NotBlank @Size(max = 50) String licenseNumber);
}
