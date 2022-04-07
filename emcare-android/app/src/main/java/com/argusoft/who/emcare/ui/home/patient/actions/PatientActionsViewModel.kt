package com.argusoft.who.emcare.ui.home.patient.actions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import com.google.android.fhir.FhirEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import javax.inject.Inject

@HiltViewModel
class PatientActionsViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val patientRepository: PatientRepository
) : ViewModel() {

    var questionnaireJson: String? = null
    private val _patientItem = MutableLiveData<ApiResponse<PatientItem>>()
    val patientItem: LiveData<ApiResponse<PatientItem>> = _patientItem

    private val _deletePatientSuccessState = MutableLiveData<Int>()
    val deletePatientSuccessState: LiveData<Int> = _deletePatientSuccessState

    private val _deletePatientLoadingState = MutableLiveData<ApiResponse<Patient>>()
    val deletePatientLoadingState: LiveData<ApiResponse<Patient>> = _deletePatientLoadingState

    private val _questionnaire = SingleLiveEvent<ApiResponse<Questionnaire>>()
    val questionnaire: LiveData<ApiResponse<Questionnaire>> = _questionnaire


    fun getPatientDetails(patientId: String?) {
        _patientItem.value = ApiResponse.Loading()
        viewModelScope.launch {
            patientRepository.getPatientDetails(patientId).collect {
                _patientItem.value = it
            }
        }
    }

    fun getQuestionnaire(questionnaireId: String) {
        _questionnaire.value = ApiResponse.Loading()
        viewModelScope.launch {
            patientRepository.getQuestionnaire(questionnaireId).collect {
                _questionnaire.value = it
            }
        }
    }

    fun deletePatient(patientId: String?) {
        _deletePatientLoadingState.value = ApiResponse.Loading()
        viewModelScope.launch {
            patientRepository.deletePatient(patientId)
        }
    }

}