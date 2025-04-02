package com.cmasproject.cmastestserver.controller.doctor;

import com.cmasproject.cmastestserver.model.AssignPatientsRequestDTO;
import com.cmasproject.cmastestserver.model.AssignedPatientsResponseDTO;
import com.cmasproject.cmastestserver.model.PatientResponseDTO;
import com.cmasproject.cmastestserver.services.DoctorService;
import com.cmasproject.cmastestserver.services.TestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;


    @GetMapping("/patients")
    public ResponseEntity<?> getPatients() {
        List<PatientResponseDTO> response = doctorService.getAllPatients();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/assignPatients")
    public ResponseEntity<?> assignPatientsToDoctor(@RequestBody AssignPatientsRequestDTO request, Authentication authentication)
    {
        String doctorUsername = authentication.getName();
        List<PatientResponseDTO> patientListResponse = doctorService.assignPatients(request.getPatientIds(), doctorUsername);
        AssignedPatientsResponseDTO response = AssignedPatientsResponseDTO.builder()
                .message("Patients assigned successfully.")
                .assignedPatients(patientListResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
