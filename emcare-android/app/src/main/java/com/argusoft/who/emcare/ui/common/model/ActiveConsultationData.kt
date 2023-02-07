package com.argusoft.who.emcare.ui.common.model

import java.util.*

data class ActiveConsultationData(
    val consultationLabel: String? = null,
    var dateOfConsultation: String? = null,
    val consultationFlowItemId: String,
    val consultationIcon: Int?,
    val header: String?,
    val consultationStage: String? = null,
    val patientId: String,
    val encounterId: String,
    val questionnaireId: String? = null,
    val structureMapId: String? = null,
    val questionnaireResponseText: String? = null,
    val isActive: Boolean = false,
)