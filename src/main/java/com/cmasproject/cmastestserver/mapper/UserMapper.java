package com.cmasproject.cmastestserver.mapper;

import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.model.registration.SignUpDoctorRequestDTO;
import com.cmasproject.cmastestserver.model.registration.SignUpPatientRequestDTO;
import com.cmasproject.cmastestserver.model.registration.SignUpPatientResponseDTO;
import com.cmasproject.cmastestserver.model.registration.SignUpDoctorResponseDTO;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    User signUpPatientRequestDTOToUser(SignUpPatientRequestDTO signUpPatientRequest);
    User signUpDoctorRequestDTOToUser(SignUpDoctorRequestDTO signUpDoctorRequest);
    SignUpPatientResponseDTO userToSignUpPatientResponseDTO(User user);
    SignUpDoctorResponseDTO userToSignUpDoctorResponseDTO(User user);
}
