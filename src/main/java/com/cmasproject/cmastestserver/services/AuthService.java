package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.model.registration.LogInRequestDTO;
import com.cmasproject.cmastestserver.model.registration.SignUpDoctorRequestDTO;
import com.cmasproject.cmastestserver.model.registration.SignUpPatientRequestDTO;
import com.cmasproject.cmastestserver.model.registration.SignUpRequestDTO;
import org.springframework.security.core.Authentication;
import org.yaml.snakeyaml.util.Tuple;

public interface AuthService {
    Tuple<User, Patient> registerPatient(SignUpPatientRequestDTO request);
    Tuple<User, Doctor> registerDoctor(SignUpDoctorRequestDTO request);

    Authentication authenticateUser(LogInRequestDTO request);
    String generateJWTToken(Authentication authentication);

    Boolean usernameExists(SignUpRequestDTO request);
    Boolean emailExists(SignUpRequestDTO request);
    Boolean phoneNumberExists(SignUpRequestDTO request);
}