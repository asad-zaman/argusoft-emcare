package com.argusoft.who.emcare.web.common.constant;

public class CommonConstant {
    public static final String EMAIL_ALREADY_EXISTS = "This Email Or Username Already Register With Em-Care";
    public static final String REGISTER_SUCCESS = "Successfully Register";
    public static final String UPDATE_SUCCESS = "Successfully Update";
    public static final String USER_NOT_FOUND = "User Not Found";
    public static final String DEFAULT_ROLE_EMCARE = "default-roles-emcare";
    public static final String ADMIN_ROLE_DESCRIPTION = "Admin Role";
    public static final String DESC = "DESC";
    public static final String ASC = "ASC";
    public static final Integer PAGE_SIZE = 10;
    public static final String LANGUAGE_KEY = "language";
    public static final String PHONE_KEY = "phone";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String FRENCH = "fr";
    public static final String HINDI = "hin";
    public static final String ENGLISH = "en";
    public static final String LOCATION_TYPE_STRING = "LOCATION";
    public static final String OPERATION_DEFINITION = "OPERATIONDEFINITION";
    public static final String CODE_SYSTEM = "CODESYSTEM";
    public static final String QUESTIONNAIRE = "QUESTIONNAIRE";
    public static final String STRUCTURE_DEFINITION = "STRUCTUREDEFINITION";
    public static final String STRUCTURE_MAP = "STRUCTUREMAP";
    public static final String LIBRARY = "LIBRARY";
    public static final String OBSERVATION = "OBSERVATION";
    public static final String MEDICATION = "MEDICATION";
    public static final String ACTIVITY_DEFINITION = "ACTIVITYDEFINITION";
    public static final String ENCOUNTER = "ENCOUNTER";
    public static final String CONDITION = "CONDITION";
    public static final String RELATED_PERSON = "RELATEDPERSON";
    public static final String ORGANIZATION_TYPE_STRING = "ORGANIZATION";
    public static final String PLANDEFINITION_TYPE_STRING = "PLANDEFINITION";
    public static final String VALUESET_TYPE_STRING = "VALUESET";
    public static final String EM_CARE_SYSTEM = "EM CARE SYSTEM";
    public static final String ACTIVE = "Active";
    public static final String INACTIVE = "Inactive";
    //    FHIR
    public static final String FHIR_PATIENT = "PATIENT";
    public static final String FHIR_QUESTIONNAIRE = "Questionnaire";
    public static final String LOCATION_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/patient-locationId";
    public static final String PRIMARY_CAREGIVER_EXTENSION_URL = "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/primary-caregiver";
    //    Exception Strings
    public static final String EM_CARE_NO_DATA_FOUND = "Data Not Found";
    public static final String MAIL_FOR_ADD_USER = "ADD_USER";
    public static final String MAIL_FOR_GENERATE_OTP = "GENERATE_OTP";
    public static final String MAIL_FOR_CONFIRMATION_EMAIL_APPROVED = "CONFIRMATION_EMAIL_APPROVED";
    public static final String MAIL_FOR_CONFIRMATION_EMAIL_REJECTED = "CONFIRMATION_EMAIL_REJECTED";
    public static final String SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME = "REGISTRATION_EMAIL_AS_USERNAME";
    public static final String SETTING_TYPE_WELCOME_EMAIL = "WELCOME_EMAIL";
    public static final String SETTING_TYPE_SEND_CONFIRMATION_EMAIL = "SEND_CONFIRMATION_EMAIL";
    public static final String RESOURCE_LAST_UPDATED_AT = "_lastUpdated";
    public static final String RESOURCE_TEXT = "_text";
    public static final String RESOURCE_ID = "_id";
    public static final String RESOURCE_FACILITY_ID = "_facilityId";
    public static final String SUMMARY = "_summary";
    public static final String RESOURCE_CONTENT = "_content";
    public static final String SUMMARY_TYPE_COUNT = "count";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String INDICATOR_DISPLAY_TYPE_COUNT = "Count";
    public static final String FHIR_TYPE_BOOLEAN_CONDITION = "Boolean";
    public static final String FHIR_TYPE_BOOLEAN_KEY = "BOOLEAN";
    public static final String FHIR_TYPE_BOOLEAN_VALUE = "valueBoolean";
    public static final String ALL_CODE = "All";

    public static final String IBM_ACCESS_KEY = "ibm.access-key";
    public static final String IBM_VERSION_DATE = "2018-05-01";
    public static final String IBM_ACCESS_URL = "ibm.access-url";
    public static final String FHIR_SERVLET = "FhirServlet";
    public static final String FHIR_SERVLET_URL_MAPPING = "/fhir/*";

    public static final String KEYCLOAK_REALM = "keycloak.realm";
    public static final String HTTPS = "https://";
    public static final String HTTP = "http://";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String CLIENT_ID = "client_id";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    public static final String URL_PREFIX = "jdbc:postgresql://";
    public static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
    public static final String DEFAULT_TENANT_ID = "Global";
    public static final String TENANT_ID = "tenantID";


    private CommonConstant() {
    }
}
