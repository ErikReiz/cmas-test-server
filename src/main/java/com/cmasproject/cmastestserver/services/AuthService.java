package com.cmasproject.cmastestserver.services;

import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.model.SignUpRequestDTO;

public interface AuthService {
    User registerUser(SignUpRequestDTO request);
    Boolean userExists(SignUpRequestDTO request);
}
