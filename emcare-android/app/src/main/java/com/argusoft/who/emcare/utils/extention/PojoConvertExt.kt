package com.argusoft.who.emcare.utils.extention

import android.content.ContentValues
import android.util.Log
import com.argusoft.who.emcare.ui.common.model.PatientItem
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.RiskAssessment

fun Patient.toPatientItem(position: Int, riskAssessment: Map<String, RiskAssessment?>): PatientItem {
    val patientId = if (hasIdElement()) idElement.idPart else ""
    val name = if (hasName()) name[0].nameAsSingleString else ""
    val gender = if (hasGenderElement()) genderElement.valueAsString else ""
    val dob = if (hasBirthDateElement()) birthDateElement.valueAsString else ""
    val identifier = if (hasIdentifier()) identifier[0].value else ""
    val line = if (hasAddress() && address[0].line.isNotEmpty()) address[0].line[0].toString() else ""
    val city = if (hasAddress()) address[0].city else ""
    val country = if (hasAddress()) address[0].country else ""
    val isActive = active
    val html: String = if (hasText()) text.div.valueAsString else ""
    val risk = riskAssessment["Patient/${patientId}"]?.let {
        it.prediction?.first()?.qualitativeRisk?.coding?.first()?.code
    }
    return PatientItem(
        id = position.toString(),
        resourceId = patientId,
        name = name,
        gender = gender ?: "",
        dob = dob ?: "",
        identifier = identifier ?: "",
        line = line ?: "",
        city = city ?: "",
        country = country ?: "",
        isActive = isActive,
        html = html,
        risk = risk
    )
}
