package com.argusoft.who.emcare.ui.home.patient.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.DATE_FORMAT
import com.argusoft.who.emcare.ui.common.model.ActiveConsultationData
import com.argusoft.who.emcare.ui.common.model.PreviousConsultationData
import com.argusoft.who.emcare.ui.common.stageToBadgeMap
import com.argusoft.who.emcare.ui.common.stageToIconMap
import com.argusoft.who.emcare.ui.home.ConsultationFlowRepository
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
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

    private val _previousConsultations = SingleLiveEvent<ApiResponse<List<PreviousConsultationData>>>()
    val previousConsultations: LiveData<ApiResponse<List<PreviousConsultationData>>> = _previousConsultations

    private val _lastConsultationDate = SingleLiveEvent<ApiResponse<String?>>()
    val lastConsultationDate: LiveData<ApiResponse<String?>> = _lastConsultationDate


    fun getLastConsultationDate(patientId: String) {
        viewModelScope.launch {
            consultationFlowRepository.getLastConsultationDateByPatientId(patientId).collect {
                _lastConsultationDate.value = it
            }
        }
    }

    fun getActiveConsultations(patientId: String){
        val consultationsArrayList = mutableListOf<ActiveConsultationData>()
        viewModelScope.launch {

            patientRepository.getPatientById(patientId).collect{ patientResponse ->
                val patientItem = patientResponse.data
                if (patientItem != null) {
                    consultationFlowRepository.getAllLatestActiveConsultationsByPatientId(patientId).collect {
                        it.data?.forEach{ consultationFlowItem ->
                            consultationFlowRepository.getConsultationSyncState(consultationFlowItem).collect {
                                isSynced ->
                                consultationsArrayList.add(
                                    ActiveConsultationData(
                                        consultationLabel = stageToBadgeMap[consultationFlowItem.consultationStage] + " Stage",
                                        dateOfConsultation = ZonedDateTime.parse(consultationFlowItem.consultationDate?.substringBefore("+").plus("Z[UTC]")).format(
                                            DateTimeFormatter.ofPattern(DATE_FORMAT)),
                                        header = stageToBadgeMap[consultationFlowItem.consultationStage],
                                        consultationIcon = stageToIconMap[consultationFlowItem.consultationStage],
                                        consultationFlowItemId = consultationFlowItem.id,
                                        patientId = consultationFlowItem.patientId,
                                        encounterId = consultationFlowItem.encounterId,
                                        questionnaireId = consultationFlowItem.questionnaireId,
                                        structureMapId = consultationFlowItem.structureMapId,
                                        consultationStage = consultationFlowItem.consultationStage,
                                        questionnaireResponseText = consultationFlowItem.questionnaireResponseText,
                                        isActive = consultationFlowItem.isActive,
                                        isSynced = isSynced.data ?: true
                                    )
                                )
                            }
                        }
                    }
                }
                _activeConsultations.value = ApiResponse.Success(consultationsArrayList,"No Active Consultations")
            }
        }
    }

    fun getPreviousConsultations(patientId: String){
        val consultationsArrayList = mutableListOf<PreviousConsultationData>()
        viewModelScope.launch {
            patientRepository.getPatientById(patientId).collect{ patientResponse ->
                val patientItem = patientResponse.data
                if (patientItem != null) {
                    consultationFlowRepository.getAllLatestInActiveConsultationsByPatientId(patientId).collect {
                        it.data?.forEach{ consultationFlowItem ->
                            consultationFlowRepository.getConsultationSyncState(consultationFlowItem).collect {
                                isSynced ->
                                consultationsArrayList.add(
                                    PreviousConsultationData(
                                        consultationLabel = stageToBadgeMap[consultationFlowItem.consultationStage] + " Stage",
                                        dateOfConsultation = ZonedDateTime.parse(consultationFlowItem.consultationDate?.substringBefore("+").plus("Z[UTC]")).format(
                                            DateTimeFormatter.ofPattern(DATE_FORMAT)),
                                        header = stageToBadgeMap[consultationFlowItem.consultationStage],
                                        consultationIcon = stageToIconMap[consultationFlowItem.consultationStage],
                                        consultationFlowItemId = consultationFlowItem.id,
                                        patientId = consultationFlowItem.patientId,
                                        encounterId = consultationFlowItem.encounterId,
                                        questionnaireId = consultationFlowItem.questionnaireId,
                                        structureMapId = consultationFlowItem.structureMapId,
                                        consultationStage = consultationFlowItem.consultationStage,
                                        questionnaireResponseText = consultationFlowItem.questionnaireResponseText,
                                        isActive = consultationFlowItem.isActive,
                                        isSynced = isSynced.data ?: true
                                    )
                                )
                            }
                        }
                    }
                }
                _previousConsultations.value = ApiResponse.Success(consultationsArrayList,"No Previous Consultations")
            }
        }
    }
}