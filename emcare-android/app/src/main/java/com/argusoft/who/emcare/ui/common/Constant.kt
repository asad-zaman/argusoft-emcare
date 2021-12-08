package com.argusoft.who.emcare.ui.common

import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.ui.common.model.Dashboard

const val INTENT_EXTRA_ALBUM = "INTENT_EXTRA_ALBUM"
const val MY_UPDATE_REQUEST_CODE = 50




//KeyCloak details
const val KEYCLOAK_CLIENT_SECRET = "1097c25b-fec0-4a96-b0a2-3a2f99b7a411"
const val KEYCLOAK_CLIENT_ID = "emcare_client"
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