package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.constants.ApplicationConstants;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.entities.enums.Role;
import com.cmasproject.cmastestserver.mapper.UserMapper;
import com.cmasproject.cmastestserver.model.LogInRequestDTO;
import com.cmasproject.cmastestserver.model.SignUpRequestDTO;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final Environment env;

    @Override
    public User registerUser(SignUpRequestDTO request)
    {
        User user = userMapper.signUpRequestDTOToUser(request);
        user.setRole(Role.PATIENT);

        user.setPasswordHash(encoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        Patient patient = Patient.builder()
                .user(savedUser)
                .dateOfBirth(request.getDateOfBirth())
                .build();

        patientRepository.save(patient);

        return savedUser;
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
    public Boolean usernameExists(SignUpRequestDTO signUpRequestDTO)
    {
        return userRepository.existsByUsername(signUpRequestDTO.getUsername());
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
