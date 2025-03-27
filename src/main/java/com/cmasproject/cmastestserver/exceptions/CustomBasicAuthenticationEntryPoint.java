package com.cmasproject.cmastestserver.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException
    {
        LocalDateTime currentTimeStamp = LocalDateTime.now();
        String message = (authException != null && authException.getMessage() != null) ? authException.getMessage()
                : "Unauthorized";
        response.setHeader("cmast-server-error-reason", "Authentication failed");
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse =
                String.format("{\"timestamp\": \"%s\", \"error\": \"%s\", \"message\": \"%s\"}",
                        currentTimeStamp, HttpStatus.BAD_REQUEST.getReasonPhrase(), message);
        response.getWriter().write(jsonResponse);
    }
}