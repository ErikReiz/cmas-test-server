package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.entities.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class DoctorRepositoryTest {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    User doctorUser;
    Doctor doctor;

    @Transactional
    @Rollback
    @BeforeEach
    public void setUp() {
        doctorUser = User.builder()
                .username("doctoruser")
                .email("doctor@example.com")
                .passwordHash("password123")
                .firstName("Doctor")
                .lastName("Test")
                .phoneNumber("+1234567890")
                .role(Role.DOCTOR)
                .build();

        userRepository.save(doctorUser);

        doctor = Doctor.builder()
                .user(doctorUser)
                .licenseNumber("12345")
                .specialty("Cardiology")
                .build();

        doctorRepository.save(doctor);
        doctorRepository.flush();
    }

    @Test
    public void testSaveDoctorWithInvalidData() {
        Doctor invalidDoctor = Doctor.builder()
                .user(doctorUser)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            doctorRepository.save(invalidDoctor);
            doctorRepository.flush();
        });
    }

    @Test
    public void testSaveDoctorWithInvalidUser() {
        Doctor invalidDoctor = Doctor.builder().build();

        assertThrows(JpaSystemException.class, () -> {
            doctorRepository.save(invalidDoctor);
            doctorRepository.flush();
        });
    }

    @Test
    public void testSaveNewDoctor() {
        assertThat(doctorRepository.existsById(doctor.getId())).isTrue();
    }

    @Test
    public void testGetDoctorByUser() {
        Doctor found = doctorRepository.findDoctorByUser(doctorUser).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getLicenseNumber()).isEqualTo("12345");
        assertThat(found.getSpecialty()).isEqualTo("Cardiology");
    }

    @Test
    public void testFindDoctorsByUser() {
        Set<Doctor> doctors = doctorRepository.findDoctorsByUser(doctorUser);
        assertThat(doctors).isNotEmpty();
        assertThat(doctors).hasSize(1);
        assertThat(doctors.iterator().next().getLicenseNumber()).isEqualTo("12345");
    }

    @Test
    public void testFindDoctorByUser() {
        Doctor found = doctorRepository.findDoctorByUser(doctorUser).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(doctor.getId());
    }

    @Test
    public void testFindDoctorByNonExistentUser() {
        User temporaryUser = User.builder()
                .id(UUID.randomUUID())
                .username("temporary")
                .email("temp@example.com")
                .passwordHash("password123")
                .firstName("Temp")
                .lastName("User")
                .phoneNumber("+1234567899")
                .version(0)
                .role(Role.DOCTOR)
                .build();

        Doctor found = doctorRepository.findDoctorByUser(temporaryUser).orElse(null);
        assertThat(found).isNull();
    }
}