package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.entities.enums.Role;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

    User user;

    @Transactional
    @Rollback
    @BeforeEach
    public void setUp() {
        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .passwordHash("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1234567890")
                .role(Role.PATIENT)
                .build();

        userRepository.save(user);
        userRepository.flush();
    }

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
    public void testSaveNewUser()
    {
        assertThat(userRepository.existsById(user.getId())).isTrue();
    }

    @Test
    public void testExistsByUsername() {
        assertThat(userRepository.existsByUsername("testuser")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistentuser")).isFalse();
    }

    @Test
    public void testExistsByEmail() {
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    public void testExistsByPhoneNumber() {
        assertThat(userRepository.existsByPhoneNumber("+1234567890")).isTrue();
        assertThat(userRepository.existsByPhoneNumber("+1234567899")).isFalse();
    }
}