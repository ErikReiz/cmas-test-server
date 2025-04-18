package com.cmasproject.cmastestserver.controller.common;

import com.cmasproject.cmastestserver.constants.ApplicationConstants;
import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.exceptions.UserAlreadyExistsException;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.mapper.UserMapper;
import com.cmasproject.cmastestserver.model.registration.*;
import com.cmasproject.cmastestserver.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.Tuple;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup/patient")
    public ResponseEntity<?> registerPatient(@Validated @RequestBody SignUpPatientRequestDTO signUpRequest)
    {
        performRegistrationChecks(signUpRequest);

        SignUpPatientResponseDTO response = authService.registerPatient(signUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signup/doctor")
    public ResponseEntity<?> registerDoctor(@Validated @RequestBody SignUpDoctorRequestDTO signUpRequest)
    {
        performDoctorRegistrationChecks(signUpRequest);

        SignUpDoctorResponseDTO response = authService.registerDoctor(signUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LogInRequestDTO logInRequest) {
        Authentication authenticationResponse = authService.authenticateUser(logInRequest);

        if(authenticationResponse == null || !authenticationResponse.isAuthenticated()) {
            throw new BadCredentialsException("Invalid password.");
        }

        String jwt = authService.generateJWTToken(authenticationResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .header(ApplicationConstants.JWT_HEADER, jwt)
                .build();
    }

    private void performDoctorRegistrationChecks(SignUpDoctorRequestDTO signUpRequest)
    {
        Map<String, String> errorMap = new HashMap<>();

        if(authService.licenseNumberExists(signUpRequest)) {
            errorMap.put("licenseNumber", "License number already exists.");
        }

        performRegistrationChecks(signUpRequest, errorMap);
    }

    private void performRegistrationChecks(SignUpRequestDTO signUpRequest)
    {
        Map<String, String> errorMap = new HashMap<>();

        performRegistrationChecks(signUpRequest, errorMap);
    }

    private void performRegistrationChecks(SignUpRequestDTO signUpRequest, Map<String, String> errorMap)
    {
        if(authService.usernameExists(signUpRequest)) {
            errorMap.put("username", "Username already exists.");
        }
        if(authService.emailExists(signUpRequest)) {
            errorMap.put("email", "Email already exists.");
        }
        if(authService.phoneNumberExists(signUpRequest)) {
            errorMap.put("phoneNumber", "Phone number already exists.");
        }

        if(!errorMap.isEmpty()){
            throw new UserAlreadyExistsException(errorMap);
        }
    }
}
