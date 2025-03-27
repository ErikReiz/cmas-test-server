package com.cmasproject.cmastestserver.controller;

import com.cmasproject.cmastestserver.constants.ApplicationConstants;
import com.cmasproject.cmastestserver.exceptions.UserAlreadyExistsException;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.mapper.UserMapper;
import com.cmasproject.cmastestserver.model.LogInRequestDTO;
import com.cmasproject.cmastestserver.model.SignUpRequestDTO;
import com.cmasproject.cmastestserver.model.SignUpResponseDTO;
import com.cmasproject.cmastestserver.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Validated @RequestBody SignUpRequestDTO signUpRequestDTO)
    {
        Map<String, String> errorMap = new HashMap<>();

        if(authService.usernameExists(signUpRequestDTO)) {
            errorMap.put("username", "Username already exists.");
        }
        if(authService.emailExists(signUpRequestDTO)) {
            errorMap.put("email", "Email already exists.");
        }
        if(authService.phoneNumberExists(signUpRequestDTO)) {
            errorMap.put("phone number", "Phone number already exists.");
        }
        if(!errorMap.isEmpty()){
            throw new UserAlreadyExistsException(errorMap);
        }

        User savedUser = authService.registerUser(signUpRequestDTO);

        SignUpResponseDTO response = userMapper.userToSignUpResponseDTO(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LogInRequestDTO request) {
        Authentication authenticationResponse = authService.authenticateUser(request);

        if(authenticationResponse == null || !authenticationResponse.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }

        String jwt = authService.generateJWTToken(authenticationResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .header(ApplicationConstants.JWT_HEADER,jwt)
                .build();
    }
}
