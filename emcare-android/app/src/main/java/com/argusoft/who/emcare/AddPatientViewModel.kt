package com.argusoft.who.emcare

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import java.util.UUID
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.*

/** ViewModel for patient registration screen {@link AddPatientFragment}. */
class AddPatientViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {

  val questionnaire: String
    get() = getQuestionnaireJson()
  val isPatientSaved = MutableLiveData<Boolean>()

  private val questionnaireResource: Questionnaire
    get() = FhirContext.forR4().newJsonParser().parseResource(questionnaire) as Questionnaire
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(application.applicationContext)
  private var questionnaireJson: String? = null

  /**
   * Saves patient registration questionnaire response into the application database.
   *
   * @param questionnaireResponse patient registration questionnaire response
   */
  fun savePatient(questionnaireResponse: QuestionnaireResponse) {
    viewModelScope.launch {
      val resources = ResourceMapper.extract(questionnaireResource, questionnaireResponse)
      val entry = ResourceMapper.extract(questionnaireResource, questionnaireResponse).entryFirstRep
      if (entry.resource !is Patient) return@launch
      val patient = entry.resource as Patient
      if (patient.identifier.isNotEmpty()
      ) {
        //Changing identifier value type from string to identifier object
        val patientIdentifier:Identifier = Identifier()
        patientIdentifier.use = Identifier.IdentifierUse.OFFICIAL
        patientIdentifier.value = questionnaireResponse.item[0].answerFirstRep.valueStringType.toString()
        patient.identifier = listOf(patientIdentifier)

        //Adding and saving caregiver details
        if(!questionnaireResponse.item[7].answer.isNullOrEmpty()) {
          val caregiver: RelatedPerson = RelatedPerson()
          caregiver.id = generateUuid()
          val caregiverHumanName: HumanName = HumanName()
          caregiverHumanName.given = listOf(questionnaireResponse.item[7].answerFirstRep.valueStringType)
          if(!questionnaireResponse.item[8].answer.isNullOrEmpty()){
            caregiverHumanName.family = questionnaireResponse.item[8].answerFirstRep.valueStringType.toString()
          }
          val caregiverReference: Reference = Reference()
          val caregiverIdentifier:Identifier = Identifier()
          caregiverIdentifier.use = Identifier.IdentifierUse.OFFICIAL
          caregiverIdentifier.value = "caregiver-" + patientIdentifier.value
          caregiver.identifier = listOf(caregiverIdentifier)
          caregiverReference.identifier = caregiverIdentifier
          val patientLinkComponent:Patient.PatientLinkComponent = Patient.PatientLinkComponent()
          patientLinkComponent.other = caregiverReference
          patient.link = listOf(patientLinkComponent)

          fhirEngine.save(caregiver)
        }
        //#Adding locationId
        val locationIdentifier:Identifier = Identifier()
        locationIdentifier.use = Identifier.IdentifierUse.OFFICIAL
        locationIdentifier.value = UUID.randomUUID().toString()
        val extension: Extension = Extension()
                                    .setValue(locationIdentifier)
                                    .setUrl("http://hl7.org/fhir/StructureDefinition/patient-locationId")
        patient.addExtension(extension)
        //End of location Id
        patient.id = generateUuid()
        val address: Address = patient.address[0]
        address.city = "GANDHINAGAR"
        address.country = "INDIA"
        patient.address = listOf(address)
        fhirEngine.save(patient)
        isPatientSaved.value = true
        return@launch
      }
      isPatientSaved.value = false
    }
  }

  private fun getQuestionnaireJson(): String {
    questionnaireJson?.let {
      return it!!
    }
    questionnaireJson = readFileFromAssets(state[AddPatientFragment.QUESTIONNAIRE_FILE_PATH_KEY]!!)
    return questionnaireJson!!
  }

  private fun readFileFromAssets(filename: String): String {
    return getApplication<Application>().assets.open(filename).bufferedReader().use {
      it.readText()
    }
  }

  private fun generateUuid(): String {
    return UUID.randomUUID().toString()
  }
}
