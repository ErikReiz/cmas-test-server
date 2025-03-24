package com.cmasproject.cmastestserver.controller;

import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.mapper.UserMapper;
import com.cmasproject.cmastestserver.model.SignUpRequestDTO;
import com.cmasproject.cmastestserver.model.SignUpResponseDTO;
import com.cmasproject.cmastestserver.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Validated @RequestBody SignUpRequestDTO signUpRequestDTO)
    {
        if(authService.userExists(signUpRequestDTO)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
        }

        User savedUser = authService.registerUser(signUpRequestDTO);

        SignUpResponseDTO response = userMapper.userToSignUpResponseDTO(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
