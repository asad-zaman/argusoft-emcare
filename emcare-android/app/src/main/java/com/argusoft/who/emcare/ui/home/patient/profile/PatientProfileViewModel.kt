package com.argusoft.who.emcare.ui.home.patient.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.ActiveConsultationData
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.ui.common.model.PreviousConsultationData
import com.argusoft.who.emcare.ui.common.stageToBadgeMap
import com.argusoft.who.emcare.ui.common.stageToIconMap
import com.argusoft.who.emcare.ui.home.ConsultationFlowRepository
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
import com.argusoft.who.emcare.utils.extention.orEmpty
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class PatientProfileViewModel @Inject constructor(
    private val consultationFlowRepository: ConsultationFlowRepository,
    private val patientRepository: PatientRepository
    ) : ViewModel() {

    private val _activeConsultations = SingleLiveEvent<ApiResponse<List<ActiveConsultationData>>>()
    val activeConsultations: LiveData<ApiResponse<List<ActiveConsultationData>>> = _activeConsultations


    fun getActiveConsultations(patientId: String){
        val consultationsArrayList = mutableListOf<ActiveConsultationData>()
        viewModelScope.launch {
            consultationFlowRepository.getAllLatestActiveConsultationsByPatientId(patientId).collect {
                it.data?.forEach{ consultationFlowItem ->
                    patientRepository.getPatientById(patientId).collect{ patientResponse ->
                        val patientItem = patientResponse.data
                        if (patientItem != null) {
                            consultationsArrayList.add(
                                ActiveConsultationData(
                                    consultationLabel = stageToBadgeMap[consultationFlowItem.consultationStage] + " Stage",
                                    dateOfConsultation = ZonedDateTime.parse(consultationFlowItem.consultationDate?.substringBefore("+").plus("Z[UTC]")).format(
                                        DateTimeFormatter.ofPattern("dd/MM/YY")),
                                    header = consultationFlowItem.questionnaireId, //TODO: For test only, replace it with appropriate header
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
                        }
                    }
                }
                _activeConsultations.value = ApiResponse.Success(consultationsArrayList,"No Active Consultations Found")
            }
        }
    }

    fun getPreviousConsultations() : ArrayList<PreviousConsultationData?>{
        return arrayListOf(
            PreviousConsultationData("Sick Child, Follow Up", "06/04/22"),
            PreviousConsultationData("Sick Child, Follow Up", "01/04/22"),
            PreviousConsultationData("Sick Child, Follow Up", "26/03/22"),
            PreviousConsultationData("Well Child Clinic", "18/03/22"),
            PreviousConsultationData("Sick Child, Follow Up", "11/03/22"),
            PreviousConsultationData("Sick Child, Follow Up", "07/01/22"),
        )
    }

}