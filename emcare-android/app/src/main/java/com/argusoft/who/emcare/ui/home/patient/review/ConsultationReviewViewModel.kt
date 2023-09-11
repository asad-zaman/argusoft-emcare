package com.argusoft.who.emcare.ui.home.patient.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.ui.home.ConsultationFlowRepository
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.get
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import javax.inject.Inject

@HiltViewModel
class ConsultationReviewViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val consultationFlowRepository: ConsultationFlowRepository
): ViewModel() {

    private val _questionnaireConsultationMap = SingleLiveEvent<MutableMap<String, ConsultationFlowItem>>()
    val questionnaireConsultationMap: LiveData<MutableMap<String, ConsultationFlowItem>> = _questionnaireConsultationMap

    private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

    fun getConsultationsWithQuestionnaire(encounterId: String) {
        val questionnaireConsultationMap = mutableMapOf<String, ConsultationFlowItem>()
        viewModelScope.launch {
            consultationFlowRepository.getConsultationFlowItemsForReview(encounterId).collect {
                it.data?.forEach {
                    val questionnaire = fhirEngine.get<Questionnaire>(it?.questionnaireId!!)
                    questionnaireConsultationMap.put(parser.encodeResourceToString(questionnaire), it)
                }
                _questionnaireConsultationMap.value = questionnaireConsultationMap
            }
        }
    }
}