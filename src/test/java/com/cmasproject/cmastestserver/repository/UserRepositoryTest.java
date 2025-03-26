package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.entities.enums.Role;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatientRepository patientRepository;

    @Test
    public void testSaveUserWithInvalidData()
    {
        User user = User.builder()
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.save(user);

            userRepository.flush();
        });
    }

    @Test
    @Transactional
    @Rollback
    public void testSaveNewUser()
    {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .passwordHash("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1234567890")
                .role(Role.PATIENT)
                .build();

        User savedUser = userRepository.save(user);
        userRepository.flush();

        assertThat(userRepository.existsById(savedUser.getId())).isTrue();

        Patient patient = Patient.builder()
                .user(savedUser)
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();

        Patient savedPatient = patientRepository.save(patient);

        assertThat(patientRepository.existsById(savedPatient.getId())).isTrue();
    }
}