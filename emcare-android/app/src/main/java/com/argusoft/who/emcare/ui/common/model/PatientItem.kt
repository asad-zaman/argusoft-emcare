package com.argusoft.who.emcare.ui.common.model

data class PatientItem(
    val id: String? = null,
    val resourceId: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val dob: String? = null,
    val identifier: String? = null,
    val line: String? = null,
    val city: String? = null,
    val country: String? = null,
    val isActive: Boolean? = null,
    val html: String? = null,
    var risk: String? = "",
    var riskItem: RiskAssessmentItem? = null,
    var isSynced: Boolean = true
)

data class RiskAssessmentItem(
    var riskStatusColor: Int,
    var riskStatus: String,
    var lastContacted: String,
    var patientCardColor: Int
)

