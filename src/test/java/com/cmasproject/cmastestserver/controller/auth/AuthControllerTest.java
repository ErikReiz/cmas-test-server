package com.cmasproject.cmastestserver.controller.auth;

import com.cmasproject.cmastestserver.constants.ApplicationConstants;
import com.cmasproject.cmastestserver.constants.TestConstants;
import com.cmasproject.cmastestserver.controller.common.AuthController;
import com.cmasproject.cmastestserver.entities.Doctor;
import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.User;
import com.cmasproject.cmastestserver.mapper.UserMapper;
import com.cmasproject.cmastestserver.model.registration.*;
import com.cmasproject.cmastestserver.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.yaml.snakeyaml.util.Tuple;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;
    @MockitoBean
    UserMapper userMapper;

    @Test
    public void testInvalidSignUpRequest() throws Exception
    {
        SignUpPatientRequestDTO signUpPatientRequest = SignUpPatientRequestDTO.builder()
                .username("")
                .password("")
                .dateOfBirth(LocalDate.parse("2026-05-15"))
                .firstName("")
                .lastName("")
                .email("")
                .phoneNumber("")
                .build();

        mockMvc.perform(post(TestConstants.SIGN_UP_PATIENT_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpPatientRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].password", hasItems("Password is required", "Password must be from 6 to 64 characters long")))
                .andExpect(jsonPath("$[*].username", hasItems("Username is required", "Username must be between 3 and 50 characters")))
                .andExpect(jsonPath("$[*].email", hasItems("Email is required")))
                .andExpect(jsonPath("$[*].firstName", hasItems("First name is required")))
                .andExpect(jsonPath("$[*].lastName", hasItems("Last name is required")));

        verify(authService, never()).usernameExists(signUpPatientRequest);
        verify(authService, never()).registerPatient(signUpPatientRequest);
        verify(userMapper, never()).userToSignUpPatientResponseDTO(any(User.class));
    }

    @Test
    public void testDuplicateUserRegistration() throws Exception
    {
        SignUpPatientRequestDTO signUpRequest = SignUpPatientRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1234567890")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();

        given(authService.usernameExists(signUpRequest)).willReturn(true);
        given(authService.emailExists(signUpRequest)).willReturn(true);
        given(authService.phoneNumberExists(signUpRequest)).willReturn(true);

        mockMvc.perform(post(TestConstants.SIGN_UP_PATIENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.username", is("Username already exists.")))
                .andExpect(jsonPath("$.email", is("Email already exists.")))
                .andExpect(jsonPath("$['phone number']", is("Phone number already exists.")));

        verify(authService).usernameExists(signUpRequest);
        verify(authService, never()).registerPatient(signUpRequest);
        verify(userMapper, never()).userToSignUpPatientResponseDTO(any(User.class));
    }

    @Test
    public void testSuccessfulPatientRegistration() throws Exception
    {
        SignUpPatientRequestDTO signUpRequest = SignUpPatientRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1234567890")
                .dateOfBirth(LocalDate.parse("1990-01-01"))
                .build();


        User savedUser = User.builder()
                .username(signUpRequest.getUsername())
                .build();

        Patient savedPatient = Patient.builder()
                .dateOfBirth(signUpRequest.getDateOfBirth())
                .build();

        SignUpPatientResponseDTO expectedResponse = SignUpPatientResponseDTO.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .dateOfBirth(signUpRequest.getDateOfBirth())
                .build();

        given(authService.usernameExists(signUpRequest)).willReturn(false);
        given(authService.emailExists(signUpRequest)).willReturn(false);
        given(authService.phoneNumberExists(signUpRequest)).willReturn(false);

        given(authService.registerPatient(signUpRequest)).willReturn(new Tuple<>(savedUser, savedPatient));
        given(userMapper.userToSignUpPatientResponseDTO(savedUser)).willReturn(expectedResponse);

        mockMvc.perform(post(TestConstants.SIGN_UP_PATIENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(authService).usernameExists(signUpRequest);
        verify(authService).registerPatient(signUpRequest);
        verify(userMapper).userToSignUpPatientResponseDTO(savedUser);
    }

    @Test
    public void testSuccessfulDoctorRegistration() throws Exception
    {
        SignUpDoctorRequestDTO signUpRequest = SignUpDoctorRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1234567890")
                .licenseNumber("123456")
                .specialty("Cardiology")
                .build();

        User savedUser = User.builder()
                .username(signUpRequest.getUsername())
                .build();

        Doctor savedDoctor = Doctor.builder()
                .licenseNumber(signUpRequest.getLicenseNumber())
                .specialty(signUpRequest.getSpecialty())
                .build();

        SignUpDoctorResponseDTO expectedResponse = SignUpDoctorResponseDTO.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .licenseNumber(signUpRequest.getLicenseNumber())
                .specialty(signUpRequest.getSpecialty())
                .build();

        given(authService.usernameExists(signUpRequest)).willReturn(false);
        given(authService.emailExists(signUpRequest)).willReturn(false);
        given(authService.phoneNumberExists(signUpRequest)).willReturn(false);

        given(authService.registerDoctor(signUpRequest)).willReturn(new Tuple<>(savedUser, savedDoctor));
        given(userMapper.userToSignUpDoctorResponseDTO(savedUser)).willReturn(expectedResponse);

        mockMvc.perform(post(TestConstants.SIGN_UP_DOCTOR_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(authService).usernameExists(signUpRequest);
        verify(authService).registerDoctor(signUpRequest);
        verify(userMapper).userToSignUpDoctorResponseDTO(savedUser);
    }

    @Test
    public void testInvalidCredentialsLogin() throws Exception {
        LogInRequestDTO loginRequest = LogInRequestDTO.builder()
                .username("user")
                .password("password")
                .build();

        Authentication mockAuth = mock(Authentication.class);

        given(mockAuth.isAuthenticated()).willReturn(false);
        given(authService.authenticateUser(any(LogInRequestDTO.class))).willReturn(mockAuth);

        mockMvc.perform(post(TestConstants.LOG_IN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials."));

        verify(authService).authenticateUser(loginRequest);
        verify(mockAuth).isAuthenticated();
        verify(authService, never()).generateJWTToken(mockAuth);
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        LogInRequestDTO loginRequest = LogInRequestDTO.builder()
                .username("user")
                .password("password")
                .build();

        Authentication mockAuth = mock(Authentication.class);
        String mockJwt = "mock-jwt-token";

        given(authService.authenticateUser(any(LogInRequestDTO.class))).willReturn(mockAuth);
        given(mockAuth.isAuthenticated()).willReturn(true);
        given(authService.generateJWTToken(mockAuth)).willReturn(mockJwt);

        mockMvc.perform(post(TestConstants.LOG_IN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string(ApplicationConstants.JWT_HEADER, is(mockJwt)));

        verify(authService).authenticateUser(loginRequest);
        verify(mockAuth).isAuthenticated();
        verify(authService).generateJWTToken(mockAuth);
    }
}