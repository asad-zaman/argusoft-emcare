package com.argusoft.who.emcare.ui.common.model

data class ConsultationItemData(
    val name: String? = null,
    val dateOfBirth: String? = "Not Given",
    val dateOfConsultation: String? = "Not Given",
    val badgeText: String? = "",
    val header: String? = "", // TODO: For testing only, replace with badgeText
    val consultationIcon: Int?,
    val consultationFlowItemId: String,
    val patientId: String? = null,
    val encounterId: String? = null,
    val questionnaireId: String? = null,
    val structureMapId: String? = null,
    val consultationStage: String? = null,
    val questionnaireResponseText: String? = null,
    val isActive: Boolean = true
)