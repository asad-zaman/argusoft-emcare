package com.argusoft.who.emcare.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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

    fun getConsultations() : ArrayList<ConsultationItemData?>{
        return arrayListOf<ConsultationItemData?>(
            ConsultationItemData("Emma Wright", "10/10/20", "01/01/22"),
            ConsultationItemData("Emily Smith", "04/05/21 ", "10/09/21"),
            ConsultationItemData("John Brown", "02/02/20", "07/06/21"),
            ConsultationItemData("Mary Clarke", "10/10/20", "10/10/21"),
        )
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