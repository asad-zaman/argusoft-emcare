package com.argusoft.who.emcare.ui.home.patient

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.LOCATION_EXTENSION_URL
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.utils.extention.toPatientItem
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.search
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PatientViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {

    var questionnaireJson: String? = null
    private val _patients = SingleLiveEvent<ApiResponse<List<PatientItem>>>()
    val patients: LiveData<ApiResponse<List<PatientItem>>> = _patients

    private val _questionnaire = SingleLiveEvent<ApiResponse<Questionnaire>>()
    val questionnaire: LiveData<ApiResponse<Questionnaire>> = _questionnaire

    private val _addPatients = MutableLiveData<ApiResponse<Int>>()
    val addPatients: LiveData<ApiResponse<Int>> = _addPatients


    fun getPatients(search: String? = null, locationId: Int?, isRefresh: Boolean = false) {
        _patients.value = ApiResponse.Loading(isRefresh)
        viewModelScope.launch {
            patientRepository.getPatients(search, locationId).collect {
                _patients.value = it
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

    fun savePatient(questionnaireResponse: QuestionnaireResponse, questionnaire: String, locationId: Int) {
        viewModelScope.launch {
            patientRepository.savePatient(questionnaireResponse, questionnaire, locationId).collect {
                _addPatients.value = it
            }
        }
    }
}