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
const val MY_UPDATE_REQUEST_CODE = 50

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
const val CONSULTATION_STAGE_REGISTRATION_ENCOUNTER = "REGISTRATION_ENCOUNTER"
const val CONSULTATION_STAGE_DANGER_SIGNS = "DANGER_SIGNS"
const val CONSULTATION_STAGE_MEASUREMENTS = "MEASUREMENTS"
const val CONSULTATION_STAGE_SYMPTOMS = "SYMPTOMS"
const val CONSULTATION_STAGE_HEALTH_PREVENTION = "HEALTH_PREVENTION"
const val CONSULTATION_STAGE_SIGNS = "SIGNS"
const val CONSULTATION_STAGE_ASSESSMENTS = "ASSESSMENTS"
const val CONSULTATION_STAGE_CLASSIFICATIONS = "CLASSIFICATIONS"

const val DEFAULT_USER_ROLE = "user"
const val DEFAULT_COUNTRY_CODE = "IQ"

val consultationFlowStageList = arrayListOf<String?>(
    CONSULTATION_STAGE_REGISTRATION_PATIENT,
    CONSULTATION_STAGE_REGISTRATION_ENCOUNTER,
    CONSULTATION_STAGE_DANGER_SIGNS,
    CONSULTATION_STAGE_MEASUREMENTS,
    CONSULTATION_STAGE_SYMPTOMS,
    CONSULTATION_STAGE_HEALTH_PREVENTION,
    CONSULTATION_STAGE_SIGNS,
    CONSULTATION_STAGE_ASSESSMENTS,
    CONSULTATION_STAGE_CLASSIFICATIONS
)
val stageToBadgeMap = mapOf(
    CONSULTATION_STAGE_REGISTRATION_PATIENT to "Registration Patient",
    CONSULTATION_STAGE_REGISTRATION_ENCOUNTER to "Registration",
    CONSULTATION_STAGE_DANGER_SIGNS to "Danger Signs",
    CONSULTATION_STAGE_MEASUREMENTS to "Measurements",
    CONSULTATION_STAGE_SYMPTOMS to "Symptoms",
    CONSULTATION_STAGE_HEALTH_PREVENTION to "HealthPrevention",
    CONSULTATION_STAGE_SIGNS to "Signs",
    CONSULTATION_STAGE_ASSESSMENTS to "Assessments",
    CONSULTATION_STAGE_CLASSIFICATIONS to "Classifications",

)

val stageToIconMap = mapOf(
    CONSULTATION_STAGE_REGISTRATION_PATIENT to R.drawable.registration_icon,
    CONSULTATION_STAGE_REGISTRATION_ENCOUNTER to R.drawable.registration_icon,
    CONSULTATION_STAGE_DANGER_SIGNS to R.drawable.danger_sign_icon,
    CONSULTATION_STAGE_MEASUREMENTS to R.drawable.measurements_icon,
    CONSULTATION_STAGE_SYMPTOMS to R.drawable.symptoms_icon,
    CONSULTATION_STAGE_HEALTH_PREVENTION to R.drawable.health_prevention_icon,
    CONSULTATION_STAGE_SIGNS to R.drawable.sign_icon,
    CONSULTATION_STAGE_ASSESSMENTS to R.drawable.tests_icon,
    CONSULTATION_STAGE_CLASSIFICATIONS to R.drawable.closed_consultation_icon_dark,
)

val stageToQuestionnaireId = mapOf(
    CONSULTATION_STAGE_REGISTRATION_PATIENT to "emcarea.registration.p",
    CONSULTATION_STAGE_REGISTRATION_ENCOUNTER to "emcareb.registration.e",
    CONSULTATION_STAGE_DANGER_SIGNS to "emcare.b7.lti-dangersigns",
    CONSULTATION_STAGE_MEASUREMENTS to "emcare.b6.measurements",
    CONSULTATION_STAGE_SYMPTOMS to "emcare.b10-14.symptoms.2m.p",
    CONSULTATION_STAGE_HEALTH_PREVENTION to "healthprevention",
    CONSULTATION_STAGE_SIGNS to "emcare.b10-16.signs.2m.p",
    CONSULTATION_STAGE_ASSESSMENTS to "emcare.b22.assessmentstests",
    CONSULTATION_STAGE_CLASSIFICATIONS to "emcare.b23.classification",
)

