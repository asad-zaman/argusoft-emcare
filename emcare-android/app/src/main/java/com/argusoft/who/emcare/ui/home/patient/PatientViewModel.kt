package com.argusoft.who.emcare.ui.home.patient

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.data.remote.fhirService.FhirPeriodicSyncWorker
import com.argusoft.who.emcare.oldstruct.toPatientItem
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.utils.extention.toPatientItem
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.search
import com.google.android.fhir.sync.Sync
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PatientViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {

    var questionnaireJson: String? = null
    private val _patients = SingleLiveEvent<ApiResponse<List<PatientItem>>>()
    val patients: LiveData<ApiResponse<List<PatientItem>>> = _patients

    private val _addPatients = MutableLiveData<ApiResponse<Int>>()
    val addPatients: LiveData<ApiResponse<Int>> = _addPatients

    init {
        getPatients()
    }

    fun getPatients(search: String? = null, isRefresh: Boolean = false) {
        _patients.value = ApiResponse.Loading(isRefresh)
        viewModelScope.launch {
            val riskAssessment = getRiskAssessments()
            _patients.value = ApiResponse.Success(data =
            fhirEngine.search<Patient> {
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
            }.mapIndexed { index, fhirPatient -> fhirPatient.toPatientItem(index + 1, riskAssessment) }
            )
        }
    }

    private suspend fun getRiskAssessments(): Map<String, RiskAssessment?> {
        return fhirEngine.search<RiskAssessment> {}.groupBy { it.subject.reference }.mapValues { entry ->
            entry
                .value
                .filter { it.hasOccurrence() }.maxByOrNull { it.occurrenceDateTimeType.value }
        }
    }

    fun syncPatients(){
        Sync.oneTimeSync<FhirPeriodicSyncWorker>(applicationContext)
    }

    fun savePatient(questionnaireResponse: QuestionnaireResponse, questionnaire: String) {
        val questionnaireResource: Questionnaire = FhirContext.forR4().newJsonParser().parseResource(questionnaire) as Questionnaire
        viewModelScope.launch {
            val resources = ResourceMapper.extract(questionnaireResource, questionnaireResponse)
            val entry = ResourceMapper.extract(questionnaireResource, questionnaireResponse).entryFirstRep
            if (entry.resource !is Patient) return@launch
            val patient = entry.resource as Patient
            if (patient.identifier.isNotEmpty()
            ) {
                _addPatients.value = ApiResponse.Loading()
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
                val locationIdentifier: Identifier = Identifier()
                locationIdentifier.use = Identifier.IdentifierUse.OFFICIAL
                locationIdentifier.value = UUID.randomUUID().toString()
                val extension: Extension = Extension()
                    .setValue(locationIdentifier)
                    .setUrl("http://hl7.org/fhir/StructureDefinition/patient-locationId")
                patient.addExtension(extension)
                //End of location Id
                fhirEngine.save(patient)
                _addPatients.value = ApiResponse.Success(1)
            }
        }
    }
}