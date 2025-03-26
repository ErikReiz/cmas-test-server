package com.cmasproject.cmastestserver.controller;

import com.cmasproject.cmastestserver.controller.exceptions.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomErrorController {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity handleUserExistsErrors(UserAlreadyExistsException exception)
    {
        return ResponseEntity.badRequest().body("User already exists.");
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity handleJPAValidation(UserAlreadyExistsException exception)
    {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Map<String, String>>> handBindErrors(MethodArgumentNotValidException exception)
    {
        List<Map<String, String>> errorList = exception.getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String > errorMap = new HashMap<>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errorMap;
                }).collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errorList);
    }
}
