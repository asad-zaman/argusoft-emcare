package com.argusoft.who.emcare.ui.common.model

data class ConsultationItemData(
    val patientId: String? = null,
    val name: String? = null,
    val dateOfBirth: String? = "Not Given",
    val dateOfConsultation: String? = "Not Given",
    val badgeText: String? = "",
    val consultationIcon: Int?,
    val header: String? = null,
    val questionnaireName: String? = null,
    val structureMapName: String? = null,
)