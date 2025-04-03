package com.cmasproject.cmastestserver.constants;

public final class TestConstants {
    public static final String AUTH_API_URL = "/api/auth";
    public static final String SIGN_UP_PATIENT_URL = AUTH_API_URL + "/signup/patient";
    public static final String SIGN_UP_DOCTOR_URL = AUTH_API_URL + "/signup/doctor";
    public static final String LOG_IN_URL = AUTH_API_URL + "/login";

    public static final String DOCTOR_API_URL = "/api/doctor";
    public static final String CREATE_TEST_RECORD_URL = DOCTOR_API_URL + "/test/create";
    public static final String ASSIGN_PATIENTS_URL = DOCTOR_API_URL + "/assignPatients";
    public static final String GET_ASSIGNED_PATIENTS_URL = DOCTOR_API_URL + "/assignedPatients";
    public static final String GET_ALL_PATIENTS_URL = DOCTOR_API_URL + "/patients";
}
