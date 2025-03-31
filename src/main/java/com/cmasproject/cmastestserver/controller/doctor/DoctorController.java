package com.cmasproject.cmastestserver.controller.doctor;

import com.cmasproject.cmastestserver.model.AssignPatientsRequestDTO;
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
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final ObjectMapper objectMapper;

    @GetMapping("/patients")
    public ResponseEntity<?> getPatients() {
        List<PatientResponseDTO> response = doctorService.getAllPatients();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/assignPatients")
    public ResponseEntity<?> assignPatientsToDoctor(@RequestBody AssignPatientsRequestDTO request, Authentication authentication) throws JsonProcessingException
    {

        String doctorUsername = authentication.getName();
        List<PatientResponseDTO> response = doctorService.assignPatients(request.getPatientIds(), doctorUsername);

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString("Patients assigned successfully." + response));
    }
}
