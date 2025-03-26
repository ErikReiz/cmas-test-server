package com.cmasproject.cmastestserver.mapper;

import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.model.SignUpRequestDTO;
import com.cmasproject.cmastestserver.model.SignUpResponseDTO;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    User signUpRequestDTOToUser(SignUpRequestDTO signUpRequestDTO);
    SignUpResponseDTO userToSignUpResponseDTO(User user);
}
