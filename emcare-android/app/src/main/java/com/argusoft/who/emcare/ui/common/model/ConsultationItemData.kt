package com.argusoft.who.emcare.ui.common.model

data class ConsultationItemData(
    val name: String? = null,
    val gender: String? = null,
    val identifier: String? = null,
    val dateOfBirth: String? = "Not Given",
    var dateOfConsultation: String,
    val badgeText: String? = "",
    val header: String? = "",
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