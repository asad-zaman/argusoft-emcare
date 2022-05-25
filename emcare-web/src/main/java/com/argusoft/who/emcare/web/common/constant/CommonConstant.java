package com.argusoft.who.emcare.web.common.constant;

public class CommonConstant {
    private CommonConstant() {
    }

    public static final String EMAIL_ALREADY_EXISTS = "This Email Or Username Already Register With Em-Care";
    public static final String REGISTER_SUCCESS = "Successfully Register";
    public static final String UPDATE_SUCCESS = "Successfully Update";
    public static final String USER_NOT_FOUND = "User Not Found";
    public static final String DEFAULT_ROLE_EMCARE = "default-roles-emcare";
    public static final String DESC = "DESC";
    public static final String ASC = "ASC";
    public static final Integer PAGE_SIZE = 10;
    public static final String LANGUAGE_KEY = "language";
    public static final String FRENCH = "fr";
    public static final String HINDI = "hin";
    public static final String ENGLISH = "en";
    public static final String LOCATION_TYPE_STRING = "LOCATION";
    public static final String ORGANIZATION_TYPE_STRING = "ORGANIZATION";
    public static final String PLANDEFINITION_TYPE_STRING = "PLANDEFINITION";
    public static final String EM_CARE_SYSTEM = "EM CARE SYSTEM";

    //    FHIR
    public static final String FHIR_PATIENT = "PATIENT";
    public static final String FHIR_QUESTIONNAIRE = "Questionnaire";

    //    Exception Strings
    public static final String EM_CARE_NO_DATA_FOUND = "Data Not Found";


    public static final String MAIL_FOR_ADD_USER = "ADD_USER";
    public static final String MAIL_FOR_GENERATE_OTP = "GENERATE_OTP";
    public static final String MAIL_FOR_CONFIRMATION_EMAIL_APPROVED = "CONFIRMATION_EMAIL_APPROVED";
    public static final String MAIL_FOR_CONFIRMATION_EMAIL_REJECTED = "CONFIRMATION_EMAIL_REJECTED";


    public static final String SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME = "Registration Email As Username";
    public static final String SETTING_TYPE_WELCOME_EMAIL = "Welcome Email";
    public static final String SETTING_TYPE_SEND_CONFIRMATION_EMAIL = "Send Confirmation Email";


}
