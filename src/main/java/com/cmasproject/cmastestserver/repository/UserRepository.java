package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Boolean existsByUsername(@NotBlank(message = "Username is required") @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") String username);

    User getUserByUsername(String username);

    Boolean existsByEmail(@NotBlank @Email String email);

    Boolean existsByPhoneNumber(@Pattern(regexp="^\\+[1-9]\\d{1,14}$") String phoneNumber);
}
