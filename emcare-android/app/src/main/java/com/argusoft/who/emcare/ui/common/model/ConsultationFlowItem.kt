package com.argusoft.who.emcare.ui.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import java.util.*

@Entity
@JsonClass(generateAdapter = true)
data class ConsultationFlowItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(), //id should be uuid
    val consultationStage: String? = null,
    val patientId: String,
    val encounterId: String,
    val questionnaireId: String? = null,
    val structureMapId: String? = null,
    val questionnaireResponseText: String? = null,
    val isActive: Boolean = false,
    var consultationDate: String? = null,
)