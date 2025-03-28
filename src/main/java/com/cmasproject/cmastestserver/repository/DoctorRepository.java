package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.print.Doc;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Doctor getDoctorByUser(User user);
}
