package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.TestRecord;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.entities.enums.Role;
import com.cmasproject.cmastestserver.entities.enums.TestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class TestRecordRepositoryTest {
    @Autowired
    private TestRecordRepository testRecordRepository;

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
    TestRecord testRecord;

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

        doctor = Doctor.builder()
                .user(doctorUser)
                .specialty("Therapist")
                .licenseNumber("12345")
                .build();

        doctorRepository.save(doctor);

        patient = Patient.builder()
                .user(patientUser)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        patientRepository.save(patient);

        testRecord = TestRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .assignedDate(LocalDateTime.now())
                .status(TestStatus.ASSIGNED)
                .build();

        testRecordRepository.save(testRecord);
        testRecordRepository.flush();
    }

    @Test
    public void testSaveTestRecordWithInvalidData() {
        TestRecord invalidRecord = TestRecord.builder().build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            testRecordRepository.save(invalidRecord);
            testRecordRepository.flush();
        });
    }

    @Test
    public void testSaveNewTestRecord() {
        assertThat(testRecordRepository.existsById(testRecord.getId())).isTrue();
    }

    @Test
    public void testGetTestRecordById() {
        TestRecord found = testRecordRepository.findTestRecordById(testRecord.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getStatus()).isEqualTo(TestStatus.ASSIGNED);
        assertThat(found.getDoctor().getUser().getUsername()).isEqualTo("doctoruser");
    }

    @Test
    public void testGetTestRecordsByPatient() {
        List<TestRecord> records = testRecordRepository.findTestRecordsByPatient(patient);
        assertThat(records).isNotEmpty();
        assertThat(records).hasSize(1);
        assertThat(records.getFirst().getId()).isEqualTo(testRecord.getId());
    }

    @Test
    public void testGetTestRecordByNonExistentId() {
        TestRecord found = testRecordRepository.findTestRecordById(UUID.randomUUID()).orElse(null);
        assertThat(found).isNull();
    }
}