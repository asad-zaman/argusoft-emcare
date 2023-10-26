package com.argusoft.who.emcare.ui.common

import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.ui.common.model.Dashboard

const val REQUEST_CODE_READ_PHONE_STATE = 1
const val INTENT_EXTRA_ALBUM = "INTENT_EXTRA_ALBUM"
const val INTENT_EXTRA_CONSULTATION_STAGE = "INTENT_EXTRA_CONSULTATION_STAGE"
const val INTENT_EXTRA_QUESTIONNAIRE_RESPONSE = "INTENT_EXTRA_QUESTIONNAIRE_RESPONSE"
const val INTENT_EXTRA_IS_ACTIVE = "INTENT_EXTRA_IS_ACTIVE"
const val INTENT_EXTRA_PATIENT_ID = "INTENT_EXTRA_PATIENT_ID"
const val INTENT_EXTRA_ENCOUNTER_ID = "INTENT_EXTRA_ENCOUNTER_ID"
const val INTENT_EXTRA_LOCATION_ID = "INTENT_EXTRA_LOCATION_ID"
const val INTENT_EXTRA_FACILITY_ID = "INTENT_EXTRA_FACILITY_ID"
const val INTENT_EXTRA_PATIENT_NAME = "INTENT_EXTRA_PATIENT_NAME"
const val INTENT_EXTRA_PATIENT_DOB = "INTENT_EXTRA_PATIENT_DOB"
const val INTENT_EXTRA_QUESTIONNAIRE_ID = "INTENT_EXTRA_QUESTIONNAIRE_ID"
const val INTENT_EXTRA_STRUCTUREMAP_ID = "INTENT_EXTRA_STRUCTUREMAP_ID"
const val INTENT_EXTRA_CONSULTATION_FLOW_ITEM_ID = "INTENT_EXTRA_CONSULTATION_FLOW_ITEM_ID"
const val INTENT_EXTRA_QUESTIONNAIRE_HEADER = "INTENT_EXTRA_QUESTIONNAIRE_HEADER"
const val INTENT_EXTRA_IS_DELETE_NEXT_CONSULTATIONS = "INTENT_EXTRA_IS_DELETE_NEXT_CONSULTATIONS"
const val MY_UPDATE_REQUEST_CODE = 50
const val DATE_FORMAT = "dd/MM/YY"
const val DATE_FORMAT_2 = "dd/MM/yyyy"
const val APP_THEME_COMPACT = -1
const val APP_THEME_COMFORTABLE = 0
const val APP_THEME_ENLARGED = 2
//URLS
const val URL_CQF_LIBRARY = "http://hl7.org/fhir/StructureDefinition/cqf-library"
const val URL_INITIAL_EXPRESSION = "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression"
const val LOCATION_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/patient-locationId"
//KeyCloak details
const val KEYCLOAK_CLIENT_SECRET = "b5a37bde-8d54-4837-a8dc-12e1f808e26e"
const val KEYCLOAK_CLIENT_ID = "emcare"
const val KEYCLOAK_SCOPE = "openid"
const val KEYCLOAK_GRANT_TYPE = "password"

val dashboardList = arrayListOf<Dashboard?>(
    Dashboard("Registration", "#F6D1CB", "#9c5950", R.drawable.ic_registration),
    Dashboard("Risk Assessment", "#AFE9ED", "#478c91", R.drawable.ic_risk_assessment),
    Dashboard("Referral", "#B9DDF5", "#5788ac", R.drawable.ic_referral),
    Dashboard("Notification", "#DFD1F5", "#6d558a", R.drawable.ic_dashboard_notification),
    Dashboard("Reports", "#FCE1C4", "#82603e", R.drawable.ic_reports),
    Dashboard("Announcements", "#C1DBD2", "#48816f", R.drawable.ic_announcements),
)

//Consultation flow
const val CONSULTATION_STAGE_REGISTRATION_PATIENT = "REGISTRATION_PATIENT"
const val CONSULTATION_STAGE_CLIENT_HISTORY = "REGISTRATION_ENCOUNTER"
const val CONSULTATION_STAGE_CONTRAINDICATIONS = "DANGER_SIGNS"
const val CONSULTATION_STAGE_ADMINISTER_VACCINE = "MEASUREMENTS"
const val CONSULTATION_STAGE_SYMPTOMS = "SYMPTOMS"
const val CONSULTATION_STAGE_SIGNS = "SIGNS"
const val CONSULTATION_STAGE_ASSESSMENTS = "ASSESSMENTS"
const val CONSULTATION_STAGE_CLASSIFICATIONS = "CLASSIFICATIONS"
const val CONSULTATION_STAGE_TREATMENTS = "TREATMENTS"

const val DEFAULT_USER_ROLE = "User"
const val DEFAULT_COUNTRY_CODE = "IQ"
const val ASSESS_SICK_CHILD_LINK_ID = "EmCare.B7-B8-B9.DE01"
const val END_CONSULTATION_CODING_VALUE =  "EmCare.B7-B8-B9.DE03"

const val EMPTY_SPACE_TO_SCROLL_LINK_ID = "empty_space_to_scroll"

//Audits
const val START_AUDIT = "Procedure Record"
const val END_AUDIT = "Procedure Record"
const val DRAFT_AUDIT = "An operation on other objects"
const val IS_LOAD_LIBRARIES = "is_load_libraries"

val consultationFlowStageList = arrayListOf<String?>(
    CONSULTATION_STAGE_REGISTRATION_PATIENT,
    CONSULTATION_STAGE_CLIENT_HISTORY,
    CONSULTATION_STAGE_CONTRAINDICATIONS,
    CONSULTATION_STAGE_ADMINISTER_VACCINE
)
val consultationFlowStageListUnderTwoMonths = arrayListOf<String?>(
    CONSULTATION_STAGE_REGISTRATION_PATIENT,
    CONSULTATION_STAGE_CLIENT_HISTORY,
    CONSULTATION_STAGE_CONTRAINDICATIONS,
    CONSULTATION_STAGE_ADMINISTER_VACCINE
)

val stageToBadgeMap = mapOf(
    CONSULTATION_STAGE_REGISTRATION_PATIENT to "Registration Patient",
    CONSULTATION_STAGE_CLIENT_HISTORY to "Client History",
    CONSULTATION_STAGE_CONTRAINDICATIONS to "Contraindications",
    CONSULTATION_STAGE_ADMINISTER_VACCINE to "Administer Vaccine",

    //UNUSED IN IMMUNISATION CONTEXT
    CONSULTATION_STAGE_SYMPTOMS to "Symptoms",
    CONSULTATION_STAGE_SIGNS to "Signs",
    CONSULTATION_STAGE_ASSESSMENTS to "Assessments",
    CONSULTATION_STAGE_CLASSIFICATIONS to "Classifications",
    CONSULTATION_STAGE_TREATMENTS to "Treatments"
)

val stageToIconMap = mapOf(
    CONSULTATION_STAGE_REGISTRATION_PATIENT to R.drawable.registration_icon,
    CONSULTATION_STAGE_CLIENT_HISTORY to R.drawable.registration_icon,
    CONSULTATION_STAGE_CONTRAINDICATIONS to R.drawable.closed_consultation_icon_dark,
    CONSULTATION_STAGE_ADMINISTER_VACCINE to R.drawable.danger_sign_icon,
    CONSULTATION_STAGE_SYMPTOMS to R.drawable.symptoms_icon,
    CONSULTATION_STAGE_SIGNS to R.drawable.sign_icon,
    CONSULTATION_STAGE_ASSESSMENTS to R.drawable.tests_icon,
    CONSULTATION_STAGE_CLASSIFICATIONS to R.drawable.measurements_icon,
    CONSULTATION_STAGE_TREATMENTS to R.drawable.treatment_icon
)

val stageToQuestionnaireId = mapOf(
    CONSULTATION_STAGE_REGISTRATION_PATIENT to "Questionnaire-IMMZCRegisterClient",
    CONSULTATION_STAGE_CLIENT_HISTORY to "IMMZD1ClientHistoryMeasles",
    CONSULTATION_STAGE_CONTRAINDICATIONS to "QIMMZD4CheckContraindicationsMeasles",
    CONSULTATION_STAGE_ADMINISTER_VACCINE to "AdministerVaccine",

    //UNUSED IN IMMUNISATION CONTEXT
    CONSULTATION_STAGE_SYMPTOMS to "emcare.b10-14.symptoms.2m.p",
    CONSULTATION_STAGE_SIGNS to "emcare.b10-16.signs.2m.p",
    CONSULTATION_STAGE_ASSESSMENTS to "emcare.b22.assessmentstests",
    CONSULTATION_STAGE_CLASSIFICATIONS to "emcare.b23.classification",
    CONSULTATION_STAGE_TREATMENTS to "emcare.treatment",
)

val stageToCareplan = mapOf(
    CONSULTATION_STAGE_CLIENT_HISTORY to "http://smart.who.int/ig/smart-immunizations-measles/PlanDefinition/IMMZD2DTMeasles2",
    CONSULTATION_STAGE_CONTRAINDICATIONS to "http://smart.who.int/ig/smart-immunizations-measles/PlanDefinition/IMMZD2DTMeaslesCI2"
)

val stageToStructureMapId = mapOf(
    CONSULTATION_STAGE_REGISTRATION_PATIENT to "IMMZCQRToPatient",
    CONSULTATION_STAGE_CLIENT_HISTORY to "IMMZD1QRToResources",
    CONSULTATION_STAGE_CONTRAINDICATIONS to "IMMZD4QRToResources",
    CONSULTATION_STAGE_ADMINISTER_VACCINE to "emcare.b6.measurements",
    CONSULTATION_STAGE_SYMPTOMS to "emcare.b10-14.symptoms.2m.p",
    CONSULTATION_STAGE_SIGNS to "emcare.b10-16.signs.2m.p",
    CONSULTATION_STAGE_ASSESSMENTS to "emcare.b22.assessmentstests",
    CONSULTATION_STAGE_CLASSIFICATIONS to "emcare.b23.classification",
    CONSULTATION_STAGE_TREATMENTS to "emcare.treatment",
    )

val stageToQuestionnaireIdUnderTwoMonths = mapOf(
    CONSULTATION_STAGE_REGISTRATION_PATIENT to "Questionnaire-IMMZCRegisterClient",
    CONSULTATION_STAGE_CLIENT_HISTORY to "IMMZD1QRToResources",
    CONSULTATION_STAGE_CONTRAINDICATIONS to "QIMMZD4CheckContraindicationsMeasles",
    CONSULTATION_STAGE_ADMINISTER_VACCINE to "emcare.b6.measurements",
    CONSULTATION_STAGE_SYMPTOMS to "emcare.b18-21.symptoms.2m.m",
    CONSULTATION_STAGE_SIGNS to "emcare.b18-21.signs.2m.m",
    CONSULTATION_STAGE_ASSESSMENTS to "emcare.b22.assessmentstests",
    CONSULTATION_STAGE_CLASSIFICATIONS to "emcare.b23.classification.m",
    CONSULTATION_STAGE_TREATMENTS to "emcare.treatment",
)

val stageToStructureMapIdUnderTwoMonths = mapOf(
    CONSULTATION_STAGE_REGISTRATION_PATIENT to "IMMZCQRToPatient",
    CONSULTATION_STAGE_CLIENT_HISTORY to "IMMZD1QRToResources",
    CONSULTATION_STAGE_CONTRAINDICATIONS to "emcare.b7.lti-dangersigns",

    //UNUSED IN IMMUNISATION CONTEXT
    CONSULTATION_STAGE_ADMINISTER_VACCINE to "emcare.b6.measurements",
    CONSULTATION_STAGE_SYMPTOMS to "emcare.b18-21.symptoms.2m.m",
    CONSULTATION_STAGE_SIGNS to "emcare.b18-21.signs.2m.m",
    CONSULTATION_STAGE_ASSESSMENTS to "emcare.b22.assessmentstests",
    CONSULTATION_STAGE_CLASSIFICATIONS to "emcare.b23.classification.m",
    CONSULTATION_STAGE_TREATMENTS to "emcare.treatment",
)