package com.argusoft.who.emcare.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.R
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
    var currentTab: Int = 0
    private val _patients = SingleLiveEvent<ApiResponse<List<PatientItem>>>()
    val patients: LiveData<ApiResponse<List<PatientItem>>> = _patients

    private val _questionnaire = SingleLiveEvent<ApiResponse<Questionnaire>>()
    val questionnaire: LiveData<ApiResponse<Questionnaire>> = _questionnaire

    private val _addPatients = MutableLiveData<ApiResponse<Int>>()
    val addPatients: LiveData<ApiResponse<Int>> = _addPatients


    fun getPatients(search: String? = null, facilityId: String?, isRefresh: Boolean = false) {
        _patients.value = ApiResponse.Loading(isRefresh)
        viewModelScope.launch {
            patientRepository.getPatients(search, facilityId).collect {
                _patients.value = it
            }
        }
    }

    fun getConsultations() : ArrayList<ConsultationItemData?>{
        return arrayListOf(
            ConsultationItemData(patientId="",name="Emma Wright", dateOfBirth="10/10/20", dateOfConsultation = "01/01/22","Consultation", consultationIcon = R.drawable.danger_sign_icon, header = "Consultation", questionnaireName = "EmCare.B10-16.Signs.2m.p" ),
            ConsultationItemData(patientId="",name="Emily Smith", dateOfBirth="04/05/21 ", dateOfConsultation = "10/09/21","Registration", consultationIcon = R.drawable.registration_icon, header = "Registration", questionnaireName = "emcarea.registration.p.august"),
            ConsultationItemData(patientId="",name="John Brown", dateOfBirth="02/02/20", dateOfConsultation = "07/06/21","Test", consultationIcon = R.drawable.measurements_icon, header="Test", questionnaireName = "default.layout"),
            ConsultationItemData(patientId="",name="Mary Clarke", dateOfBirth="10/10/20", dateOfConsultation = "10/10/21","Closed", consultationIcon = R.drawable.closed_consultation_icon_dark, header = "Symptoms", questionnaireName = "emcare.b10-14.symptoms.2m.p"),
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