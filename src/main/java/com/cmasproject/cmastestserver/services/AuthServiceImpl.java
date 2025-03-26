package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.entities.enums.Role;
import com.cmasproject.cmastestserver.mapper.UserMapper;
import com.cmasproject.cmastestserver.model.SignUpRequestDTO;
import com.cmasproject.cmastestserver.repository.PatientRepository;
import com.cmasproject.cmastestserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;

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
    public Boolean userExists(SignUpRequestDTO signUpRequestDTO)
    {
        return userRepository.existsByUsername(signUpRequestDTO.getUsername());
    }
}
