package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.model.registration.*;
import org.springframework.security.core.Authentication;
import org.yaml.snakeyaml.util.Tuple;

public interface AuthService {
    SignUpPatientResponseDTO registerPatient(SignUpPatientRequestDTO request);
    SignUpDoctorResponseDTO registerDoctor(SignUpDoctorRequestDTO request);

    Authentication authenticateUser(LogInRequestDTO request);
    String generateJWTToken(Authentication authentication);

    Boolean usernameExists(SignUpRequestDTO request);
    Boolean emailExists(SignUpRequestDTO request);
    Boolean phoneNumberExists(SignUpRequestDTO request);
}