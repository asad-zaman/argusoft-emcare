package com.argusoft.who.emcare.ui.home.patient.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.ui.common.model.SidepaneItem
import com.argusoft.who.emcare.ui.home.ConsultationFlowRepository
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
import com.argusoft.who.emcare.utils.extention.orEmpty
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class PreviousConsultationQuestionnaireViewModel @Inject constructor(
    private val consultationFlowRepository: ConsultationFlowRepository,
    private val patientRepository: PatientRepository
) : ViewModel(){

    var questionnaireJson: String? = null
    val parser = FhirContext.forR4().newJsonParser()


    private val _patient = SingleLiveEvent<ApiResponse<Patient>>()
    val patient: LiveData<ApiResponse<Patient>> = _patient

    private val _questionnaire = SingleLiveEvent<ApiResponse<String>>()
    val questionnaire: LiveData<ApiResponse<String>> = _questionnaire

    private val _sidepaneItems = SingleLiveEvent<ApiResponse<List<SidepaneItem>>>()
    val sidepaneItems: LiveData<ApiResponse<List<SidepaneItem>>> = _sidepaneItems

    fun getPatient(patientId: String) {
        viewModelScope.launch {
            patientRepository.getPatient(patientId).collect {
                _patient.value = it
            }
        }
    }

    fun getSidePaneItems(encounterId: String, patientId: String) {
        viewModelScope.launch {
            val sidepaneList = mutableListOf<SidepaneItem>()
            consultationFlowRepository.getAllConsultationsByEncounterId(encounterId).collect{
                patientRepository.getPatientById(patientId).collect { patientResponse ->
                    val patientItem = patientResponse.data!!
                    consultationFlowStageList.forEach { stage ->
                        val consultationFlowItems = it.data?.filter { consultationFlowItem -> consultationFlowItem.consultationStage.equals(stage) }
                        val consultationFlowItem = consultationFlowItems?.firstOrNull()
                        if(!stage.equals(CONSULTATION_STAGE_REGISTRATION_PATIENT)){
                            if(consultationFlowItem != null) {
                                sidepaneList.add(
                                    SidepaneItem(
                                        stageToIconMap[stage],
                                    stageToBadgeMap[stage],
                                    ConsultationItemData(
                                        name = patientItem.nameFirstRep.nameAsSingleString.orEmpty { patientItem.identifierFirstRep.value ?:"NA #${patientItem.id?.takeLast(9)}"},
                                        gender = patientItem.genderElement?.valueAsString ,
                                        identifier = patientItem.identifierFirstRep.value ,
                                        dateOfBirth = patientItem.birthDateElement.valueAsString ?: "Not Provided",
                                        dateOfConsultation = ZonedDateTime.parse(consultationFlowItem.consultationDate?.substringBefore("+").plus("Z[UTC]")).format(
                                            DateTimeFormatter.ofPattern(DATE_FORMAT)),
                                        badgeText = stageToBadgeMap[consultationFlowItem.consultationStage],
                                        header = stageToBadgeMap[consultationFlowItem.consultationStage],
                                        consultationIcon = stageToIconMap[consultationFlowItem.consultationStage],
                                        consultationFlowItemId = consultationFlowItem.id,
                                        patientId = consultationFlowItem.patientId,
                                        encounterId = consultationFlowItem.encounterId,
                                        questionnaireId = consultationFlowItem.questionnaireId,
                                        structureMapId = consultationFlowItem.structureMapId,
                                        consultationStage = consultationFlowItem.consultationStage,
                                        questionnaireResponseText = consultationFlowItem.questionnaireResponseText,
                                        isActive = consultationFlowItem.isActive
                                    )
                                    )
                                )
                            } else {
                                sidepaneList.add(SidepaneItem(stageToIconMap[stage], stageToBadgeMap[stage]))
                            }
                        }
                    }
                    _sidepaneItems.value = ApiResponse.Success(sidepaneList)
                }
            }
        }
    }

    fun cleanQuestionnairePair(pair: Pair<String, String>) : Pair<String, String> {
        val questionnaire = pair.first
        val questionnaireResponse = pair.second

        //get items in questionnaire
        val questionnaireLinkIds = mutableListOf<String>()
        (parser.parseResource(questionnaire) as Questionnaire).item.forEach { item ->
            questionnaireLinkIds.add(item.linkId)
        }

        val questionnaireResponseItems = mutableListOf<QuestionnaireResponse.QuestionnaireResponseItemComponent>()
        questionnaireResponseItems.addAll((parser.parseResource(questionnaireResponse) as QuestionnaireResponse).item)

        val cleanedQuestionnaireResponseObject = (parser.parseResource(questionnaireResponse) as QuestionnaireResponse).setItem(questionnaireResponseItems.filter {
            questionnaireLinkIds.contains(it.linkId)
        })

        return questionnaire to parser.encodeResourceToString(cleanedQuestionnaireResponseObject)

    }

    fun getClosedQuestionnaire(questionnaireId: String) {
        _questionnaire.value = ApiResponse.Loading()
        viewModelScope.launch {
            patientRepository.getQuestionnaire(questionnaireId).collect {
                val questionnaireString = parser.encodeResourceToString(it.data!!)
                _questionnaire.value = ApiResponse.Success(data=questionnaireString)
            }
        }
    }

}