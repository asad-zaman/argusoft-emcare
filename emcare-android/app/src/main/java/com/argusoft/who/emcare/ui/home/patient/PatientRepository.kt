package com.argusoft.who.emcare.ui.home.patient

import android.app.Application
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.LOCATION_EXTENSION_URL
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.utils.extention.toPatientItem
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.search
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.*
import java.util.*
import javax.inject.Inject

class PatientRepository @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val application: Application
) {

    fun getQuestionnaire(questionnaireId: String) = flow {
        emit(ApiResponse.Success(data = fhirEngine.load(Questionnaire::class.java, questionnaireId)))
    }

    fun getPatients(search: String? = null, locationId: Int?) = flow {
        val riskAssessment = getRiskAssessments()
        val list = fhirEngine.search<Patient> {
            if (!search.isNullOrEmpty())
                filter(
                    Patient.NAME,
                    {
                        modifier = StringFilterModifier.CONTAINS
                        value = search
                    }
                )
            sort(Patient.GIVEN, Order.ASCENDING)
            count = 100
            from = 0
        }.filter {
            (it.getExtensionByUrl(LOCATION_EXTENSION_URL)?.value as? Identifier)?.value == locationId.toString()
        }.mapIndexed { index, fhirPatient ->
            fhirPatient.toPatientItem(index + 1, riskAssessment)
        }
        emit(ApiResponse.Success(data = list))
    }

    private suspend fun getRiskAssessments(): Map<String, RiskAssessment?> {
        return fhirEngine.search<RiskAssessment> {}.groupBy { it.subject.reference }.mapValues { entry ->
            entry
                .value
                .filter { it.hasOccurrence() }.maxByOrNull { it.occurrenceDateTimeType.value }
        }
    }

    fun getPatientDetails(patientId: String?) = flow {
        if (patientId != null) {
            emit(ApiResponse.Success(fhirEngine.load(Patient::class.java, patientId).convertPatientToPatientItem()))
        }
    }


    fun savePatient(questionnaireResponse: QuestionnaireResponse, questionnaire: String, locationId: Int) = flow {
        val questionnaireResource: Questionnaire = FhirContext.forR4().newJsonParser().parseResource(questionnaire) as Questionnaire
        val entry = ResourceMapper.extract(application, questionnaireResource, questionnaireResponse).entryFirstRep
        if (entry.resource !is Patient) return@flow
        val patient = entry.resource as Patient
        if (patient.identifier.isNotEmpty()
        ) {
            emit(ApiResponse.Loading())
            //Adding id
            patient.id = UUID.randomUUID().toString()

            //Changing identifier value type from string to identifier object
            val patientIdentifier: Identifier = Identifier()
            patientIdentifier.use = Identifier.IdentifierUse.OFFICIAL
            patientIdentifier.value = questionnaireResponse.item[0].item[0].answerFirstRep.valueStringType.toString()
            patient.identifier = listOf(patientIdentifier)

            //Adding and saving caregiver details
            if (!questionnaireResponse.item[2].item[0].answer.isNullOrEmpty()) {
                val caregiver: RelatedPerson = RelatedPerson()
                caregiver.id = UUID.randomUUID().toString()
                val caregiverHumanName: HumanName = HumanName()
                caregiverHumanName.given = listOf(questionnaireResponse.item[2].item[0].answerFirstRep.valueStringType)
                if (!questionnaireResponse.item[2].item[1].answer.isNullOrEmpty()) {
                    caregiverHumanName.family = questionnaireResponse.item[2].item[1].answerFirstRep.valueStringType.toString()
                }
                caregiver.name = listOf(caregiverHumanName)

                //Saving Caregiver
                fhirEngine.save(caregiver)

                //adding caregiver reference to the patient
                val caregiverReference: Reference = Reference()
                val caregiverIdentifier: Identifier = Identifier()
                caregiverIdentifier.use = Identifier.IdentifierUse.OFFICIAL
                caregiverIdentifier.value = caregiver.id
                caregiver.identifier = listOf(caregiverIdentifier)
                caregiverReference.identifier = caregiverIdentifier
                val patientLinkComponent: Patient.PatientLinkComponent = Patient.PatientLinkComponent()
                patientLinkComponent.other = caregiverReference
                patient.link = listOf(patientLinkComponent)
            }
            //#Adding locationId
            val locationIdentifier = Identifier()
            locationIdentifier.use = Identifier.IdentifierUse.OFFICIAL
            locationIdentifier.value = locationId.toString()
            val extension: Extension = Extension()
                .setValue(locationIdentifier)
                .setUrl(LOCATION_EXTENSION_URL)
            patient.addExtension(extension)
            //End of location Id
            fhirEngine.save(patient)
            emit(ApiResponse.Success(1))
        }
    }

    fun deletePatient(patientId: String?) = flow {
        if (patientId != null) {
            fhirEngine.remove(Patient::class.java, patientId)
        }
        emit(ApiResponse.Success(1))
    }

    private fun Patient.convertPatientToPatientItem(): PatientItem {

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

        return PatientItem(
            id = patientId,
            name = name,
            gender = gender,
            dob = dob,
            identifier = identifier,
            line = line,
            city = city,
            country = country,
            isActive = isActive,
            html = html
        )

    }
}