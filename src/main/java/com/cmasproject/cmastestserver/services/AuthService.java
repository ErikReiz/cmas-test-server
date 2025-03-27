package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.model.LogInRequestDTO;
import com.cmasproject.cmastestserver.model.SignUpRequestDTO;
import org.springframework.security.core.Authentication;

public interface AuthService {
    User registerUser(SignUpRequestDTO request);
    Authentication authenticateUser(LogInRequestDTO request);
    String generateJWTToken(Authentication authentication);

    Boolean usernameExists(SignUpRequestDTO request);
    Boolean emailExists(SignUpRequestDTO request);
    Boolean phoneNumberExists(SignUpRequestDTO request);
}
