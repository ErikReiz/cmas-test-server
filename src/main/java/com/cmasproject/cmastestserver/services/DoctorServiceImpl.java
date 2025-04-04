package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.entities.enums.Role;
import com.cmasproject.cmastestserver.mapper.PatientMapper;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;
import com.cmasproject.cmastestserver.repository.DoctorRepository;
import com.cmasproject.cmastestserver.repository.PatientRepository;
import com.cmasproject.cmastestserver.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService, PatientMapper {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Override
    public List<PatientResponseDTO> getAllPatients()
    {
        return patientRepository.findAll()
                .stream().map(this::mapToPatientResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientResponseDTO> assignPatients(List<UUID> patientIds, String doctorUsername)
    {
        User doctorUser = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new EntityNotFoundException("Could not find User entity for username: " + doctorUsername));
        Doctor doctor = doctorRepository.findDoctorByUser(doctorUser)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Doctor entity for username: " + doctorUsername));

        List<Patient> patients = patientRepository.findAllById(patientIds);
        doctor.setPatients(new HashSet<>(patients));
        return doctorRepository.save(doctor).getPatients().stream()
                .map(this::mapToPatientResponseDTO)
                .collect(Collectors.toList());
    }

    public Set<PatientResponseDTO> getAssignedPatients(String doctorUsername)
    {
        User user = userRepository.getUserByUsername(doctorUsername);
        return patientRepository.getPatientsByDoctors(Set.of(doctorRepository.findDoctorByUser(user)))
                .stream().map(this::mapToPatientResponseDTO)
                .collect(Collectors.toSet());
    }
}
