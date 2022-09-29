package com.argusoft.who.emcare.ui.common

import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.ui.common.model.Dashboard

const val REQUEST_CODE_READ_PHONE_STATE = 1
const val INTENT_EXTRA_ALBUM = "INTENT_EXTRA_ALBUM"
const val INTENT_EXTRA_PATIENT_ID = "INTENT_EXTRA_PATIENT_ID"
const val INTENT_EXTRA_ENCOUNTER_ID = "INTENT_EXTRA_ENCOUNTER_ID"
const val INTENT_EXTRA_LOCATION_ID = "INTENT_EXTRA_LOCATION_ID"
const val INTENT_EXTRA_FACILITY_ID = "INTENT_EXTRA_FACILITY_ID"
const val INTENT_EXTRA_PATIENT_NAME = "INTENT_EXTRA_PATIENT_NAME"
const val INTENT_EXTRA_PATIENT_DOB = "INTENT_EXTRA_PATIENT_DOB"
const val INTENT_EXTRA_QUESTIONNAIRE_NAME = "INTENT_EXTRA_QUESTIONNAIRE_NAME"
const val INTENT_EXTRA_STRUCTUREMAP_NAME = "INTENT_EXTRA_STRUCTUREMAP_NAME"
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

val consultationFlowQuestionnaireList = arrayListOf<String?>(
    "Registration",
    "Signs",
    "Symptoms",
    "Danger Signs",
    "Measurements",
    "Health Prevention",
    "Classification"
)

