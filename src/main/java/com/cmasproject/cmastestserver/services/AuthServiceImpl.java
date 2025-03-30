package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.constants.ApplicationConstants;
import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.entities.enums.Role;
import com.cmasproject.cmastestserver.mapper.UserMapper;
import com.cmasproject.cmastestserver.model.registration.*;
import com.cmasproject.cmastestserver.repository.DoctorRepository;
import com.cmasproject.cmastestserver.repository.PatientRepository;
import com.cmasproject.cmastestserver.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.Tuple;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final Environment env;

    @Override
    public SignUpPatientResponseDTO registerPatient(SignUpPatientRequestDTO request)
    {
        User user = userMapper.signUpPatientRequestDTOToUser(request);
        user.setRole(Role.PATIENT);

        user.setPasswordHash(encoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        Patient patient = Patient.builder()
                .user(savedUser)
                .dateOfBirth(request.getDateOfBirth())
                .build();

        Patient savedPatient = patientRepository.save(patient);

        SignUpPatientResponseDTO response = userMapper.userToSignUpPatientResponseDTO(savedUser);
        response.setDateOfBirth(savedPatient.getDateOfBirth());

        return response;
    }


    @Override
    public SignUpDoctorResponseDTO registerDoctor(SignUpDoctorRequestDTO request)
    {
        User user = userMapper.signUpDoctorRequestDTOToUser(request);
        user.setRole(Role.DOCTOR);

        user.setPasswordHash(encoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        Doctor doctor = Doctor.builder()
                .user(savedUser)
                .licenseNumber(request.getLicenseNumber())
                .specialty(request.getSpecialty())
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        SignUpDoctorResponseDTO response = userMapper.userToSignUpDoctorResponseDTO(savedUser);
        response.setLicenseNumber(savedDoctor.getLicenseNumber());
        response.setSpecialty(savedDoctor.getSpecialty());

        return response;
    }

    @Override
    public Authentication authenticateUser(LogInRequestDTO request)
    {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(request.getUsername(), request.getPassword());
        return authenticationManager.authenticate(authentication);
    }

    @Override
    public String generateJWTToken(Authentication authentication)
    {
        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY, ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        int expirationTimeHours = Integer.parseInt(env.getProperty(ApplicationConstants.JWT_EXPIRATION_KEY, String.valueOf(ApplicationConstants.JWT_EXPIRATION_TIME_HOURS)));

        return Jwts.builder().issuer("CMAS TEST SERVER").subject("JWT Token")
                .claim("username", authentication.getName())
                .claim("authorities", authentication.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + hoursToMilliseconds(expirationTimeHours)))
                .signWith(secretKey).compact();
    }


    @Override
    public Boolean usernameExists(SignUpRequestDTO signUpPatientRequest)
    {
        return userRepository.existsByUsername(signUpPatientRequest.getUsername());
    }

    @Override
    public Boolean emailExists(SignUpRequestDTO request)
    {
        return userRepository.existsByEmail(request.getEmail());
    }

    @Override
    public Boolean phoneNumberExists(SignUpRequestDTO request)
    {
        return userRepository.existsByPhoneNumber(request.getPhoneNumber());
    }

    private int hoursToMilliseconds(int hours)
    {
        return hours * 60 * 60 * 1000;
    }
}
