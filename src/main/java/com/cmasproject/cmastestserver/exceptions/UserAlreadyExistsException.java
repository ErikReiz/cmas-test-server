package com.cmasproject.cmastestserver.exceptions;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class UserAlreadyExistsException extends RuntimeException {
    private final Map<String, String> errors;

    public Map<String, String> getErrors() {
        return errors;
    }
}
