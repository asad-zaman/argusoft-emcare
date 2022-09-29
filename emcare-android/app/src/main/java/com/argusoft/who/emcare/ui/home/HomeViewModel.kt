package com.argusoft.who.emcare.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.URL_CQF_LIBRARY
import com.argusoft.who.emcare.ui.common.URL_INITIAL_EXPRESSION
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
import com.argusoft.who.emcare.utils.extention.orEmpty
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.common.datatype.asStringValue
import com.google.android.fhir.datacapture.createQuestionnaireResponseItem
import com.google.android.fhir.workflow.FhirOperator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.*
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val fhirEngine: FhirEngine
) : ViewModel() {

    var questionnaireJson: String? = null
    var currentTab: Int = 0
    private val _patients = SingleLiveEvent<ApiResponse<List<PatientItem>>>()
    val patients: LiveData<ApiResponse<List<PatientItem>>> = _patients

    private val _consultations = SingleLiveEvent<ApiResponse<List<ConsultationItemData>>>()
    val consultations: LiveData<ApiResponse<List<ConsultationItemData>>> = _consultations

    private val _questionnaireWithQR = SingleLiveEvent<ApiResponse<Pair<String, String>>>()
    val questionnaireWithQR: LiveData<ApiResponse<Pair<String, String>>> = _questionnaireWithQR

    private val _saveQuestionnaire = MutableLiveData<ApiResponse<Int>>()
    val saveQuestionnaire: LiveData<ApiResponse<Int>> = _saveQuestionnaire


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


    fun getQuestionnaireWithQR(questionnaireId: String, questionnairePatientId: String? = null, questionnaireEncounterId: String? = null) {
        _questionnaireWithQR.value = ApiResponse.Loading()
        viewModelScope.launch {
            var patientId = questionnairePatientId
            var encounterId = questionnaireEncounterId
            patientRepository.getQuestionnaire(questionnaireId).collect {
                val questionnaireJsonWithQR: Questionnaire = preProcessQuestionnaire(it.data!!, patientId)

                if(patientId == null && encounterId == null){ //TODO: on save persist this information
                    patientId = UUID.randomUUID().toString()
                    encounterId = UUID.randomUUID().toString()
                }

                val questionnaireResponse: QuestionnaireResponse = generateQuestionnaireResponseWithPatientIdAndEncounterId(questionnaireJsonWithQR, patientId!!, encounterId!!)

                val questionnaireString = FhirContext.forR4().newJsonParser().encodeResourceToString(questionnaireJsonWithQR)
                val questionnaireResponseString = FhirContext.forR4().newJsonParser().encodeResourceToString(questionnaireResponse)
                _questionnaireWithQR.value = ApiResponse.Success(data=questionnaireString to questionnaireResponseString)
            }
        }
    }

    fun saveQuestionnaire(questionnaireResponse: QuestionnaireResponse, questionnaire: String, facilityId: String, patientId: String? = null, encounterId: String? = null) {
        viewModelScope.launch {
            patientRepository.saveQuestionnaire(questionnaireResponse, questionnaire,facilityId, patientId, encounterId).collect {
                _saveQuestionnaire.value = it
            }
        }
    }

    private fun generateQuestionnaireResponseWithPatientIdAndEncounterId(questionnaireJson: Questionnaire, patientId: String, encounterId: String) : QuestionnaireResponse {
       //Create empty QR as done in the SDC
        val questionnaireResponse:QuestionnaireResponse = QuestionnaireResponse().apply {
            questionnaire = questionnaireJson.url
        }
        questionnaireJson.item.forEach { it2 ->
            questionnaireResponse.addItem(it2.createQuestionnaireResponseItem())
        }

        //Inject patientId as subject & encounterId as Encounter.
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

        return questionnaireResponse
    }

    private fun preProcessQuestionnaire(questionnaire: Questionnaire, patientId: String? = null) : Questionnaire {
        var ansQuestionnaire = injectUuid(questionnaire)
        if(!patientId.isNullOrEmpty()){
            ansQuestionnaire = injectInitialExpressionCqlValues(questionnaire, patientId)
        }
        return ansQuestionnaire
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

    private fun injectInitialExpressionCqlValues(questionnaire: Questionnaire, patientId: String): Questionnaire {
        var cqlLibraryURL = ""
        val expressionSet = mutableSetOf<String>()
        //Check if questionnaire has CQF-library
        if(questionnaire.hasExtension(URL_CQF_LIBRARY))
            cqlLibraryURL = questionnaire.getExtensionByUrl(URL_CQF_LIBRARY).value.asStringValue()
        //If questionnaire has Cql library then evaluate library and inject the parameters
        if(cqlLibraryURL.isNotEmpty()){
            //Creating ExpressionSet
            questionnaire.item.forEach { item ->
                if(item.hasExtension(URL_INITIAL_EXPRESSION)) {
                    expressionSet.add((item.getExtensionByUrl(URL_INITIAL_EXPRESSION).value as Expression).expression)
                }
            }
            //Evaluating Library
            val parameters = FhirOperator(FhirContext.forR4(), fhirEngine)
                .evaluateLibrary(cqlLibraryURL, patientId, expressionSet)
                as Parameters
            //Inject parameters to appropriate places
            questionnaire.item.forEach { item ->
                if(item.hasExtension(URL_INITIAL_EXPRESSION)) {
                    val value = parameters.getParameter((item.getExtensionByUrl(URL_INITIAL_EXPRESSION).value as Expression).expression) //parameter has same name as linkId
                    //inject value in the QuestionnaireItem as InitialComponent
                    if(value != null)
                        item.initial = mutableListOf(Questionnaire.QuestionnaireItemInitialComponent(value))
                }
            }
            return questionnaire
        } else {
            return questionnaire
        }
    }
}