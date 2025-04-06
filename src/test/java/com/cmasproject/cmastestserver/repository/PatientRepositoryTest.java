package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.entities.enums.Role;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class PatientRepositoryTest {
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    User patientUser;
    User doctorUser;
    Patient patient;
    Doctor doctor;

    @Transactional
    @Rollback
    @BeforeEach
    public void setUp() {
        patientUser = User.builder()
                .username("patientuser")
                .email("patient@example.com")
                .passwordHash("password123")
                .firstName("Patient")
                .lastName("Test")
                .phoneNumber("+1234567891")
                .role(Role.PATIENT)
                .build();

        doctorUser = User.builder()
                .username("doctoruser")
                .email("doctor@example.com")
                .passwordHash("password123")
                .firstName("Doctor")
                .lastName("Test")
                .phoneNumber("+1234567890")
                .role(Role.DOCTOR)
                .build();

        userRepository.save(patientUser);
        userRepository.save(doctorUser);

        patient = Patient.builder()
                .user(patientUser)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        patientRepository.save(patient);

        doctor = Doctor.builder()
                .user(doctorUser)
                .licenseNumber("12345")
                .specialty("Cardiology")
                .build();

        Set<Patient> patients = new HashSet<>();
        patients.add(patient);
        doctor.setPatients(patients);

        doctorRepository.save(doctor);
        patientRepository.flush();
        doctorRepository.flush();
    }

    @Test
    public void testSavePatientWithInvalidData() {
        Patient invalidPatient = Patient.builder()
                .user(patientUser)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            patientRepository.save(invalidPatient);
            patientRepository.flush();
        });
    }

    @Test
    public void testSavePatientWithInvalidUser() {
        Patient invalidPatient = Patient.builder().build();

        assertThrows(JpaSystemException.class, () -> {
            patientRepository.save(invalidPatient);
            patientRepository.flush();
        });
    }

    @Test
    public void testSaveNewPatient() {
        assertThat(patientRepository.existsById(patient.getId())).isTrue();
    }

    @Test
    public void testGetPatientById() {
        Patient found = patientRepository.findPatientById(patient.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getUser().getUsername()).isEqualTo("patientuser");
        assertThat(found.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testGetPatientByUser() {
        Patient found = patientRepository.findPatientByUser(patientUser).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(patient.getId());
    }

    @Test
    public void testGetPatientsByDoctors() {
        Set<Doctor> doctors = new HashSet<>();
        doctors.add(doctor);

        Set<Patient> patients = patientRepository.findPatientsByDoctors(doctors);
        assertThat(patients).isNotEmpty();
        assertThat(patients).hasSize(1);
        assertThat(patients.iterator().next().getId()).isEqualTo(patient.getId());
    }

    @Test
    public void testGetPatientByNonExistentId() {
        Patient found = patientRepository.findPatientById(UUID.randomUUID()).orElse(null);
        assertThat(found).isNull();
    }
}