package com.cmasproject.cmastestserver.constants;

public final class TestConstants {
    public static final String AUTH_API_URL = "/api/auth";
    public static final String SIGN_UP_PATIENT_URL = AUTH_API_URL + "/signup/patient";
    public static final String SIGN_UP_DOCTOR_URL = AUTH_API_URL + "/signup/doctor";
    public static final String LOG_IN_URL = AUTH_API_URL + "/login";

    public static final String CREATE_TEST_API_URL = "/api/doctor";
    public static final String CREATE_TEST_RECORD_URL = CREATE_TEST_API_URL + "/test/create";
}
