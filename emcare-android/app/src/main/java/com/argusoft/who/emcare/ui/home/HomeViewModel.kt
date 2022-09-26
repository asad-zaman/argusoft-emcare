package com.argusoft.who.emcare.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
import com.argusoft.who.emcare.utils.extention.orEmpty
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import com.google.android.fhir.datacapture.common.datatype.asStringValue
import com.google.android.fhir.datacapture.createQuestionnaireResponseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {

    var questionnaireJson: String? = null
    var currentTab: Int = 0
    private val _patients = SingleLiveEvent<ApiResponse<List<PatientItem>>>()
    val patients: LiveData<ApiResponse<List<PatientItem>>> = _patients

    private val _consultations = SingleLiveEvent<ApiResponse<List<ConsultationItemData>>>()
    val consultations: LiveData<ApiResponse<List<ConsultationItemData>>> = _consultations

    private val _questionnaire = SingleLiveEvent<ApiResponse<Questionnaire>>()
    val questionnaire: LiveData<ApiResponse<Questionnaire>> = _questionnaire

    private val _questionnaireWithQR = SingleLiveEvent<ApiResponse<Pair<String, String>>>()
    val questionnaireWithQR: LiveData<ApiResponse<Pair<String, String>>> = _questionnaireWithQR

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

    fun getConsultations(search: String? = null, facilityId: String?, isRefresh: Boolean = false){
        _consultations.value = ApiResponse.Loading(isRefresh)
        val consultationsArrayList = mutableListOf(
            ConsultationItemData(patientId="",
                name="Helsenorge Reg.",
                dateOfBirth="10/10/20",
                dateOfConsultation = "10/10/21",
                badgeText = "Registration",
                consultationIcon = R.drawable.closed_consultation_icon_dark,
                header = "Registration",
                questionnaireName = "registration.ideal.q"),
            ConsultationItemData(patientId="",
                name="Signs",
                dateOfBirth="10/10/20",
                dateOfConsultation = "01/01/22",
                badgeText = "Signs",
                consultationIcon = R.drawable.danger_sign_icon,
                header = "Signs",
                questionnaireName = "EmCare.B10-16.Signs.2m.p" ),
//            ConsultationItemData(patientId="",
//                name="Symptoms-p",
//                dateOfBirth="10/10/20",
//                dateOfConsultation = "10/10/21",
//                badgeText = "Symptoms",
//                consultationIcon = R.drawable.symptoms_icon,
//                header = "Symptoms",
//                questionnaireName = "emcare.b10-14.symptoms.2m.p"),
//            ConsultationItemData(patientId="",
//                name="Symptoms-m",
//                dateOfBirth="10/10/20",
//                dateOfConsultation = "10/10/21",
//                badgeText = "Symptoms",
//                consultationIcon = R.drawable.symptoms_icon,
//                header = "Symptoms",
//                questionnaireName = "emcare.b18-21.symptoms.2m.m"),
//            ConsultationItemData(patientId="",
//                name="Danger Signs",
//                dateOfBirth="10/10/20",
//                dateOfConsultation = "10/10/21",
//                badgeText = "Danger Signs",
//                consultationIcon = R.drawable.symptoms_icon,
//                header = "Danger Signs",
//                questionnaireName = "emcare.b7.lti-dangersigns"),
//            ConsultationItemData(patientId="",
//                name="Health Preventions",
//                dateOfBirth="10/10/20",
//                dateOfConsultation = "10/10/21",
//                badgeText = "healthprevention",
//                consultationIcon = R.drawable.symptoms_icon,
//                header = "healthprevention",
//                questionnaireName = "healthprevention"),
            ConsultationItemData(patientId="",
                name="Measurements",
                dateOfBirth="10/10/20",
                dateOfConsultation = "10/10/21",
                badgeText = "Measurements",
                consultationIcon = R.drawable.closed_consultation_icon_dark,
                header = "Measurements",
                questionnaireName = "emcare.b6.measurements"),
        )
        viewModelScope.launch {
            patientRepository.getPatients(search, facilityId).collect {
                it.data?.forEach {  patientItem ->
                    consultationsArrayList.add(
                        ConsultationItemData(
                            patientId = patientItem.id,
                            name = patientItem.name.orEmpty { patientItem.identifier ?:"NA #${patientItem.resourceId?.takeLast(9)}"},
                            dateOfBirth = patientItem.dob ?: "21/06/99",
                            dateOfConsultation = SimpleDateFormat("dd/MM/YY").format(Date()),
                            badgeText = "Signs",
                            consultationIcon = R.drawable.sign_icon,
                            header = "Signs",
                            questionnaireName = "EmCare.B10-16.Signs.2m.p"
                        )
                    )
                }
                _consultations.value = ApiResponse.Success(consultationsArrayList)
            }
        }

    }

    fun getQuestionnaire(questionnaireId: String) {
        _questionnaire.value = ApiResponse.Loading()
        viewModelScope.launch {
            patientRepository.getQuestionnaire(questionnaireId).collect {
                _questionnaire.value = ApiResponse.Success(data=injectUuid(it.data!!))
            }
        }
    }

    fun getQuestionnaireWithQR(questionnaireId: String) {
        _questionnaireWithQR.value = ApiResponse.Loading()
        viewModelScope.launch {
            patientRepository.getQuestionnaire(questionnaireId).collect {
                val questionnaireJsonWithQR: Questionnaire = injectUuid(it.data!!)
                val patientId = UUID.randomUUID().toString()
                val encounterId = UUID.randomUUID().toString()
                val questionnaireResponse:QuestionnaireResponse = QuestionnaireResponse().apply {
                    questionnaire = questionnaireJsonWithQR.url
                }
                questionnaireJsonWithQR.item.forEach { it2 ->
                    questionnaireResponse.addItem(it2.createQuestionnaireResponseItem())
                }
                questionnaireResponse.subject = Reference().apply {
                    id = IdType(patientId).id
                    type = ResourceType.Patient.name
                    identifier = Identifier().apply {
                        value = patientId
                    }
                }
                questionnaireResponse.encounter = Reference().apply {
                    id = encounterId
                    type = ResourceType.Encounter.name
                    identifier = Identifier().apply {
                        value = encounterId
                    }
                }
//                val questionnaireResponse = ResourceMapper.populate(questionnaireJsonWithQR,
//                    Patient().apply {
//                    id = patientId
//                }, Encounter().apply {
//                    id = encounterId
//                })
                val questionnaireString = FhirContext.forR4().newJsonParser().encodeResourceToString(questionnaireJsonWithQR)
                val questionnaireResponseString = FhirContext.forR4().newJsonParser().encodeResourceToString(questionnaireResponse)
                _questionnaireWithQR.value = ApiResponse.Success(data=questionnaireString to questionnaireResponseString)
            }
        }
    }

    fun savePatient(questionnaireResponse: QuestionnaireResponse, questionnaire: String, facilityId: String) {
        viewModelScope.launch {
            patientRepository.savePatient(questionnaireResponse, questionnaire, facilityId).collect {
                _addPatients.value = it
            }
        }
    }

    private fun injectUuid(questionnaire: Questionnaire) : Questionnaire {
        questionnaire.item.forEach { item ->
            if(!item.initial.isNullOrEmpty()) {
                if(item.initial[0].value.asStringValue() == "uuid()") {
                    item.initial =
                        mutableListOf(Questionnaire.QuestionnaireItemInitialComponent(StringType(
                            UUID.randomUUID().toString())))
                }
            }
        }
        return questionnaire
    }
}