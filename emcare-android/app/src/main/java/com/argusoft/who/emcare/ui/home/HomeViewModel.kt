package com.argusoft.who.emcare.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.di.AppModule
import com.argusoft.who.emcare.ui.common.CONSULTATION_STAGE_REGISTRATION_ENCOUNTER
import com.argusoft.who.emcare.ui.common.CONSULTATION_STAGE_REGISTRATION_PATIENT
import com.argusoft.who.emcare.ui.common.DATE_FORMAT
import com.argusoft.who.emcare.ui.common.EMPTY_SPACE_TO_SCROLL_LINK_ID
import com.argusoft.who.emcare.ui.common.URL_CQF_LIBRARY
import com.argusoft.who.emcare.ui.common.URL_INITIAL_EXPRESSION
import com.argusoft.who.emcare.ui.common.consultationFlowStageList
import com.argusoft.who.emcare.ui.common.consultationFlowStageListUnderTwoMonths
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.ui.common.model.SidepaneItem
import com.argusoft.who.emcare.ui.common.stageToBadgeMap
import com.argusoft.who.emcare.ui.common.stageToIconMap
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
import com.argusoft.who.emcare.utils.extention.orEmpty
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.extensions.allItems
import com.google.android.fhir.datacapture.extensions.asStringValue
import com.google.android.fhir.datacapture.extensions.createQuestionnaireResponseItem
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.search
import com.google.android.fhir.workflow.FhirOperator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.StringType
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val consultationFlowRepository: ConsultationFlowRepository,
    private val fhirOperator: FhirOperator,
    private val fhirEngine: FhirEngine,
    private val libraryRepository: LibraryRepository,
    private val knowledgeManager: KnowledgeManager,
    @ApplicationContext private val context: Context,
    private val preference: Preference,
    @AppModule.IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    var questionnaireJson: String? = null
    var currentTab: Int = 0

    private val _librariesLoaded = SingleLiveEvent<ApiResponse<Int>>()
    val librariesLoaded: LiveData<ApiResponse<Int>> = _librariesLoaded

    private val _patient = SingleLiveEvent<ApiResponse<Patient>>()
    val patient: LiveData<ApiResponse<Patient>> = _patient

    private val _deleteNextConsultations = SingleLiveEvent<ApiResponse<String>>()
    val deleteNextConsultations: LiveData<ApiResponse<String>> = _deleteNextConsultations

    private val _draftQuestionnaire = SingleLiveEvent<ApiResponse<String>>()
    val draftQuestionnaire: LiveData<ApiResponse<String>> = _draftQuestionnaire

    private val _draftQuestionnaireNew = SingleLiveEvent<ApiResponse<String>>()
    val draftQuestionnaireNew: LiveData<ApiResponse<String>> = _draftQuestionnaireNew

    private val _patients = SingleLiveEvent<ApiResponse<List<PatientItem>>>()
    val patients: LiveData<ApiResponse<List<PatientItem>>> = _patients

    private val _consultations = SingleLiveEvent<ApiResponse<List<ConsultationItemData>>>()
    val consultations: LiveData<ApiResponse<List<ConsultationItemData>>> = _consultations

    private val _questionnaireWithQR = SingleLiveEvent<ApiResponse<Pair<String, String>>>()
    val questionnaireWithQR: LiveData<ApiResponse<Pair<String, String>>> = _questionnaireWithQR

    private val _saveQuestionnaire = MutableLiveData<ApiResponse<ConsultationFlowItem>>()
    val saveQuestionnaire: LiveData<ApiResponse<ConsultationFlowItem>> = _saveQuestionnaire

    private val _nextQuestionnaire = MutableLiveData<ApiResponse<ConsultationFlowItem>>()
    val nextQuestionnaire: LiveData<ApiResponse<ConsultationFlowItem>> = _nextQuestionnaire

    private val _sidepaneItems = SingleLiveEvent<ApiResponse<List<SidepaneItem>>>()
    val sidepaneItems: LiveData<ApiResponse<List<SidepaneItem>>> = _sidepaneItems

    private val _unsyncedResourcesCount = SingleLiveEvent<ApiResponse<Int>>()
    val unsyncedResourcesCount: LiveData<ApiResponse<Int>> = _unsyncedResourcesCount

    fun getPatients(search: String? = null, facilityId: String?, isRefresh: Boolean = false) {
        _patients.value = ApiResponse.Loading(isRefresh)
        viewModelScope.launch {
            patientRepository.getPatients(search, facilityId).collect {
                _patients.value = it
            }
        }
    }

    fun loadLibraries(isReloadHomeActivity: Boolean) {
        viewModelScope.launch {

            libraryRepository.getLibraries().collect {
                val librariesList = it.data
                librariesList?.forEach { library ->
                    knowledgeManager.install(writeToFile(library))
                }
            }.let {
                _librariesLoaded.value = ApiResponse.Success(if (isReloadHomeActivity) 1 else 0)
            }
        }
    }

    private suspend fun clearKnowledgeManagerDatabase() = withContext(dispatcher) {
        knowledgeManager.clearDatabase()
    }

    private fun writeToFile(library: Library): File {
        return File(
            context.filesDir,
            if (library.name == null) library.title else library.name
        ).apply {
            writeText(FhirContext.forR4().newJsonParser().encodeResourceToString(library))
        }
    }

    private fun getGmtTimeFromLastSyncTime(): String {
        val formatStringGmt: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val formatStringLocal: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a")
        return if (preference.getLastSyncTimestamp().isNotEmpty()) {
            formatStringGmt.timeZone = TimeZone.getTimeZone("gmt")
            formatStringGmt.format(formatStringLocal.parse(preference.getLastSyncTimestamp()))
        } else {
            ""
        }
    }

    fun checkUnsycnedResources() {
        val lastSyncTimestamp = getGmtTimeFromLastSyncTime()
        if (lastSyncTimestamp.isNotEmpty()) {
            viewModelScope.launch {
                consultationFlowRepository.getConsultationCountAfterTimestamp(lastSyncTimestamp)
                    .collect {
                        val count = it.data!!
                        _unsyncedResourcesCount.value = ApiResponse.Success(data = count!!)
                    }
            }
        } else {
            _unsyncedResourcesCount.value = ApiResponse.Success(data = 0)
        }
    }


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
            consultationFlowRepository.getAllConsultationsByEncounterId(encounterId).collect {
                patientRepository.getPatientById(patientId).collect { patientResponse ->
                    val patientItem = patientResponse.data!!

                    var currentConsultationFLowList = consultationFlowStageList
                    val encounterConsultationItem = it.data?.filter { consultationFlowItem ->
                        consultationFlowItem.consultationStage.equals(
                            CONSULTATION_STAGE_REGISTRATION_ENCOUNTER
                        )
                    }
                    if (patientItem.hasBirthDate()) {
                        var isAgeUnderTwoMonths = patientItem.birthDate.toInstant()
                            .isAfter(Instant.now().minusSeconds(3600 * 24 * 60))
                        //Taking care of case when consultation has started for under two months and on reopenin the child is now over two months.
                        if (encounterConsultationItem?.isNotEmpty() == true) {
                            isAgeUnderTwoMonths =
                                patientItem.birthDate.toInstant()
                                    .plusMillis(
                                        (ZonedDateTime.parse(
                                            encounterConsultationItem[0].consultationDate?.substringBefore(
                                                "+"
                                            ).plus("Z[UTC]")
                                        ).toInstant().toEpochMilli()
                                                - Instant.now().toEpochMilli()) * 1000
                                    )
                                    .isAfter(Instant.now().minusSeconds(3600 * 24 * 60))
                        }
                        if (isAgeUnderTwoMonths) {
                            currentConsultationFLowList = consultationFlowStageListUnderTwoMonths
                        }
                    }

                    currentConsultationFLowList.forEach { stage ->
                        val consultationFlowItems = it.data?.filter { consultationFlowItem ->
                            consultationFlowItem.consultationStage.equals(stage)
                        }
                        val consultationFlowItem = consultationFlowItems?.firstOrNull()
                        if (!stage.equals(CONSULTATION_STAGE_REGISTRATION_PATIENT)) {
                            if (consultationFlowItem != null) {
                                sidepaneList.add(
                                    SidepaneItem(
                                        stageToIconMap[stage],
                                        stageToBadgeMap[stage],
                                        ConsultationItemData(
                                            name = patientItem.nameFirstRep.nameAsSingleString.orEmpty {
                                                patientItem.identifierFirstRep.value
                                                    ?: "NA #${patientItem.id?.takeLast(9)}"
                                            },
                                            gender = patientItem.genderElement?.valueAsString,
                                            identifier = patientItem.identifierFirstRep.value,
                                            dateOfBirth = patientItem.birthDateElement.valueAsString
                                                ?: "Not Provided",
                                            dateOfConsultation = ZonedDateTime.parse(
                                                consultationFlowItem.consultationDate?.substringBefore(
                                                    "+"
                                                ).plus("Z[UTC]")
                                            ).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
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
                                sidepaneList.add(
                                    SidepaneItem(
                                        stageToIconMap[stage],
                                        stageToBadgeMap[stage]
                                    )
                                )
                            }
                        }
                    }
                    _sidepaneItems.value = ApiResponse.Success(sidepaneList)
                }
            }
        }
    }

    fun getConsultations(search: String? = null, isRefresh: Boolean = false) {
        _consultations.value = ApiResponse.Loading(isRefresh)
        val consultationsArrayList = mutableListOf<ConsultationItemData>()
        viewModelScope.launch {
            consultationFlowRepository.getAllLatestActiveConsultations().collect {
                it.data?.forEach { consultationFlowItem ->
                    consultationFlowRepository.getConsultationSyncState(consultationFlowItem)
                        .collect { isSynced ->
                            patientRepository.getPatientById(consultationFlowItem.patientId)
                                .collect { patientResponse ->
                                    val patientItem = patientResponse.data
                                    if (patientItem != null) {
                                        consultationsArrayList.add(
                                            ConsultationItemData(
                                                name = patientItem.nameFirstRep.nameAsSingleString.orEmpty {
                                                    patientItem.identifierFirstRep.value
                                                        ?: "#${patientItem.id?.take(9)}"
                                                },
                                                gender = patientItem.genderElement?.valueAsString,
                                                identifier = patientItem.identifierFirstRep.value,
                                                dateOfBirth = patientItem.birthDateElement.valueAsString
                                                    ?: "Not Provided",
                                                dateOfConsultation = ZonedDateTime.parse(
                                                    consultationFlowItem.consultationDate?.substringBefore(
                                                        "+"
                                                    ).plus("Z[UTC]")
                                                ).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
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
                                                isActive = consultationFlowItem.isActive,
                                                isSynced = isSynced.data ?: true
                                            )
                                        )
                                    }
                                }
                        }

                }
                _consultations.value =
                    ApiResponse.Success(consultationsArrayList.filter { consultationItemData ->
                        if (consultationItemData.identifier != null) {
                            search?.let { it1 ->
                                consultationItemData.name?.contains(
                                    it1,
                                    ignoreCase = true
                                )!! || consultationItemData.identifier.equals(
                                    it1,
                                    ignoreCase = true
                                )
                            }!!
                        } else {
                            search?.let { it1 ->
                                consultationItemData.name?.contains(
                                    it1,
                                    ignoreCase = true
                                )
                            }!!
                        }
                    }, "No Active Consultations Found")
            }
        }
    }

    fun deleteNextConsultations(consultationFlowItemId: String, encounterId: String) {
        viewModelScope.launch {
            patientRepository.deleteNextConsultations(consultationFlowItemId, encounterId).collect {
                _deleteNextConsultations.value = it
            }
        }
    }

    fun saveQuestionnaireAsDraft(
        consultationFlowItemId: String,
        questionnaireResponse: QuestionnaireResponse
    ) {
        viewModelScope.launch {
            patientRepository.updateConsultationQuestionnaireResponse(
                consultationFlowItemId,
                questionnaireResponse
            ).collect {
                _draftQuestionnaire.value = it
            }
        }
    }

    fun saveQuestionnaireAsDraftNew(questionnaireResponse: QuestionnaireResponse) {
        viewModelScope.launch {
            patientRepository.saveNewConsultation(questionnaireResponse).collect {
                _draftQuestionnaireNew.value = ApiResponse.Success(it)
            }
        }
    }


    fun getQuestionnaireWithQR(
        questionnaireId: String,
        patientId: String,
        encounterId: String,
        isPreviouslySavedConsultation: Boolean,
        previousQuestionnaireResponse: String? = ""
    ) {
        _questionnaireWithQR.value = ApiResponse.Loading()
        viewModelScope.launch {
            patientRepository.getQuestionnaire(questionnaireId).collect {
                val parser = FhirContext.forR4().newJsonParser()
                val questionnaireJsonWithQR: Questionnaire? = preProcessQuestionnaire(
                    it.data!!,
                    patientId,
                    encounterId,
                    isPreviouslySavedConsultation
                )
                if (questionnaireJsonWithQR == null) {
                    _questionnaireWithQR.value =
                        ApiResponse.ApiError(apiErrorMessageResId = R.string.initial_expression_error)
                } else {
                    var questionnaireResponse: QuestionnaireResponse = QuestionnaireResponse()
                    questionnaireResponse = if (isPreviouslySavedConsultation) {
                        addHiddenQuestionnaireItemsWithNestedItems(
                            previousQuestionnaireResponse!!,
                            questionnaireJsonWithQR
                        )
                    } else
                        generateQuestionnaireResponseWithPatientIdAndEncounterId(
                            questionnaireJsonWithQR,
                            patientId!!,
                            encounterId!!
                        )
                    val questionnaireString = parser.encodeResourceToString(questionnaireJsonWithQR)
                    val questionnaireResponseString =
                        parser.encodeResourceToString(questionnaireResponse)
                    _questionnaireWithQR.value =
                        ApiResponse.Success(data = questionnaireString to questionnaireResponseString)
                }
            }
        }
    }


    fun moveToNextQuestionnaire(consultationFlowItemId: String, encounterId: String) {
        _questionnaireWithQR.value = ApiResponse.Loading()
        viewModelScope.launch {
            consultationFlowRepository.getNextConsultationByConsultationIdAndEncounterId(
                consultationFlowItemId,
                encounterId
            ).collect {
                _nextQuestionnaire.value = it
            }
        }
    }

    fun saveQuestionnaire(
        questionnaireResponse: QuestionnaireResponse,
        questionnaire: String,
        facilityId: String,
        structureMapId: String,
        consultationFlowItemId: String? = null,
        consultationStage: String? = null
    ) {
        viewModelScope.launch {
            patientRepository.saveQuestionnaire(
                questionnaireResponse,
                questionnaire,
                facilityId,
                structureMapId,
                consultationFlowItemId,
                consultationStage
            ).collect {
                _saveQuestionnaire.value = it
            }
        }
    }

    private fun generateQuestionnaireResponseWithPatientIdAndEncounterId(
        questionnaireJson: Questionnaire,
        patientId: String,
        encounterId: String
    ): QuestionnaireResponse {
        //Create empty QR as done in the SDC
        val questionnaireResponse: QuestionnaireResponse = QuestionnaireResponse().apply {
            questionnaire = questionnaireJson.url
        }
        questionnaireJson.item.forEach { it2 ->
            questionnaireResponse.addItem(it2.createQuestionnaireResponseItem())
        }

        //Inject patientId as subject & encounterId as Encounter.
        questionnaireResponse.subject = Reference().apply {
            id = IdType(patientId).id
            reference = "/Patient/${patientId}"
            type = ResourceType.Patient.name
            identifier = Identifier().apply {
                value = patientId
            }
        }
        questionnaireResponse.encounter = Reference().apply {
            id = encounterId
            reference = "/Encounter/${encounterId}"
            type = ResourceType.Encounter.name
            identifier = Identifier().apply {
                value = encounterId
            }
        }

        return questionnaireResponse
    }

    private fun addHiddenQuestionnaireItemsWithNestedItems(
        previousQuestionnaireResponse: String,
        questionnaire: Questionnaire
    ): QuestionnaireResponse {
        val previousQuestionnaireResponseObject =
            FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
                .parseResource(QuestionnaireResponse::class.java, previousQuestionnaireResponse)
        val questionnaireLinkIdList = mutableListOf<String>()

        //Adding link id of all questionnaire items
        questionnaire.item.forEach { item ->
            questionnaireLinkIdList.add(item.linkId)
        }

        //Fetching list of all questionnaire items
        val questionnaireResponseItemsList = mutableListOf<QuestionnaireResponseItemComponent>()
        questionnaireResponseItemsList.addAll(previousQuestionnaireResponseObject.allItems)

        val finalQuestionnaireResponseItemsList =
            mutableListOf<QuestionnaireResponseItemComponent>()
        var matchingQuestionnaireResponseItem: QuestionnaireResponseItemComponent?
        var matchingQuestionnaireItem: Questionnaire.QuestionnaireItemComponent?

        //Setting items with previously answered questions through link id
        questionnaireLinkIdList.forEachIndexed { index, linkId ->
            try {
                matchingQuestionnaireResponseItem =
                    questionnaireResponseItemsList.firstOrNull { questionnaireResponseItem ->
                        linkId == questionnaireResponseItem.linkId
                    }
                matchingQuestionnaireItem = questionnaire.item.firstOrNull { questionnaireItem ->
                    linkId == questionnaireItem.linkId
                }
                val finalQuestionnaireResponseNestedItemsList =
                    mutableListOf<QuestionnaireResponseItemComponent>()

                if (matchingQuestionnaireResponseItem != null) {
                    if (matchingQuestionnaireItem?.item?.size!! > 0) {
                        matchingQuestionnaireItem?.item?.forEachIndexed { index, questionnaireItemComponent ->
                            var matchingQuestionnaireResponseNestedItem =
                                matchingQuestionnaireResponseItem?.item?.firstOrNull { questionnaireResponseItem ->
                                    questionnaireItemComponent.linkId == questionnaireResponseItem.linkId
                                }
                            if (matchingQuestionnaireResponseNestedItem != null) {
                                finalQuestionnaireResponseNestedItemsList.add(
                                    matchingQuestionnaireResponseNestedItem
                                )
                            } else {
                                finalQuestionnaireResponseNestedItemsList.add(
                                    QuestionnaireResponseItemComponent(
                                        StringType(questionnaireItemComponent.linkId)
                                    )
                                )
                            }
                        }
                        matchingQuestionnaireResponseItem?.item =
                            finalQuestionnaireResponseNestedItemsList
                    }
                    finalQuestionnaireResponseItemsList.add(matchingQuestionnaireResponseItem!!)
                } else {
                    val newItem = QuestionnaireResponseItemComponent(StringType(linkId))
                    var matchingQuestionnaireNestedItem =
                        questionnaire.item.firstOrNull { questionnaireItem ->
                            linkId == questionnaireItem.linkId
                        }
                    if (matchingQuestionnaireNestedItem != null) {
                        matchingQuestionnaireNestedItem.item.forEachIndexed { index, questionnaireItemComponent ->
                            finalQuestionnaireResponseNestedItemsList.add(
                                QuestionnaireResponseItemComponent(
                                    StringType(questionnaireItemComponent.linkId)
                                )
                            )
                        }
                        newItem.item = finalQuestionnaireResponseNestedItemsList
                    }
                    questionnaireResponseItemsList.add(index, newItem)
                    finalQuestionnaireResponseItemsList.add(newItem)
                }
            } catch (e: java.lang.IndexOutOfBoundsException) {
                questionnaireResponseItemsList.add(
                    index,
                    QuestionnaireResponseItemComponent(StringType(linkId))
                )
                finalQuestionnaireResponseItemsList.add(
                    QuestionnaireResponseItemComponent(
                        StringType(linkId)
                    )
                )
            }
        }

        previousQuestionnaireResponseObject.item = finalQuestionnaireResponseItemsList
        return previousQuestionnaireResponseObject
    }


    private fun addHiddenQuestionnaireItems(
        previousQuestionnaireResponse: String,
        questionnaire: Questionnaire
    ): QuestionnaireResponse {
        val previousQuestionnaireResponseObject =
            FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
                .parseResource(QuestionnaireResponse::class.java, previousQuestionnaireResponse)
        val questionnaireLinkIdList = mutableListOf<String>()
        val groupLinkIdMap = mutableMapOf<String, MutableList<String>>()
        //Adding link id of all questionnaire items
        questionnaire.item.forEach { item ->
            questionnaireLinkIdList.add(item.linkId)
            if (item.type.equals("group") && item.hasItem()) {
                item.item.forEach { nestedItem ->
                    val groupLinkIdList: MutableList<String> =
                        if (groupLinkIdMap.containsKey(item.linkId)) groupLinkIdMap[item.linkId]!! else mutableListOf<String>()
                    groupLinkIdList.add(nestedItem.linkId)
                    groupLinkIdMap[item.linkId] = groupLinkIdList
                }
            }
        }

        //Fetching list of all questionnaire items
        val questionnaireResponseItemsList = mutableListOf<QuestionnaireResponseItemComponent>()
        questionnaireResponseItemsList.addAll(previousQuestionnaireResponseObject.allItems)

        val finalQuestionnaireResponseItemsList =
            mutableListOf<QuestionnaireResponseItemComponent>()

        //Setting items with previously answered questions through link id
        questionnaireLinkIdList.forEachIndexed { index, linkId ->
            try {
                val matchingQuestionnaireResponseItem =
                    questionnaireResponseItemsList.firstOrNull { questionnaireResponseItem ->
                        linkId == questionnaireResponseItem.linkId
                    }
                if (matchingQuestionnaireResponseItem != null) {
                    if (groupLinkIdMap[linkId] != null) {
                        groupLinkIdMap[linkId]?.forEach { nestedItemLinkId ->
                            val nestedItem =
                                matchingQuestionnaireResponseItem.item.firstOrNull { _ ->
                                    linkId == nestedItemLinkId
                                }
                            if (nestedItem == null) {
                                matchingQuestionnaireResponseItem.addItem(
                                    QuestionnaireResponseItemComponent(StringType(linkId))
                                )
                            }
                        }
                    }
                    finalQuestionnaireResponseItemsList.add(matchingQuestionnaireResponseItem)
                } else {
                    val questionnaireResponseItemToAdd =
                        QuestionnaireResponseItemComponent(StringType(linkId))
                    if (groupLinkIdMap[linkId] != null) {
                        groupLinkIdMap[linkId]?.forEach { nestedItemLinkId ->
                            questionnaireResponseItemToAdd.addItem(
                                QuestionnaireResponseItemComponent(StringType(nestedItemLinkId))
                            )
                        }
                    }
                    questionnaireResponseItemsList.add(index, questionnaireResponseItemToAdd)
                    finalQuestionnaireResponseItemsList.add(questionnaireResponseItemToAdd)
                }
            } catch (e: java.lang.IndexOutOfBoundsException) {
                val questionnaireResponseItemToAdd =
                    QuestionnaireResponseItemComponent(StringType(linkId))
                if (groupLinkIdMap[linkId] != null) {
                    groupLinkIdMap[linkId]?.forEach { nestedItemLinkId ->
                        questionnaireResponseItemToAdd.addItem(
                            QuestionnaireResponseItemComponent(StringType(nestedItemLinkId))
                        )
                    }
                }
                questionnaireResponseItemsList.add(index, questionnaireResponseItemToAdd)
                finalQuestionnaireResponseItemsList.add(questionnaireResponseItemToAdd)
            }
        }



        previousQuestionnaireResponseObject.item = finalQuestionnaireResponseItemsList
        return previousQuestionnaireResponseObject
    }


    private suspend fun preProcessQuestionnaire(
        questionnaire: Questionnaire,
        patientId: String,
        encounterId: String,
        isPreviouslySavedConsultation: Boolean
    ): Questionnaire? {
        var ansQuestionnaire: Questionnaire? = injectUuid(questionnaire)
        ansQuestionnaire = addEmptySpaceToScroll(ansQuestionnaire!!)
        if (questionnaire.hasExtension(URL_CQF_LIBRARY) && !isPreviouslySavedConsultation) {
            ansQuestionnaire =
                injectInitialExpressionCqlValues(ansQuestionnaire!!, patientId, encounterId)
        }
        return ansQuestionnaire
    }

    private fun addEmptySpaceToScroll(questionnaire: Questionnaire): Questionnaire {
        questionnaire.item.add(Questionnaire.QuestionnaireItemComponent().apply {
            linkId = EMPTY_SPACE_TO_SCROLL_LINK_ID
            type = Questionnaire.QuestionnaireItemType.DISPLAY
            text = "<br><br><br><br><br><br><br>"
        })
        return questionnaire
    }

    private fun injectUuid(questionnaire: Questionnaire): Questionnaire {
        questionnaire.item.forEach { item ->
            if (!item.initial.isNullOrEmpty() && item.initial[0].value.asStringValue() == "uuid()") {
                item.initial =
                    mutableListOf(
                        Questionnaire.QuestionnaireItemInitialComponent(
                            StringType(
                                UUID.randomUUID().toString()
                            )
                        )
                    )
            }
        }
        return questionnaire
    }

    private suspend fun injectInitialExpressionCqlValues(
        questionnaire: Questionnaire,
        patientId: String,
        encounterId: String
    ): Questionnaire? = withContext(dispatcher) {
        try {
            val cqlLibraryURL =
                questionnaire.getExtensionByUrl(URL_CQF_LIBRARY).value.asStringValue()
            val expressionSet = mutableSetOf<String>()
            //If questionnaire has Cql library then evaluate library and inject the parameters
            if (cqlLibraryURL.isNotEmpty()) {
                //Creating ExpressionSet
                questionnaire.item.forEach { item ->
                    if (item.hasExtension(URL_INITIAL_EXPRESSION)) {
                        expressionSet.add((item.getExtensionByUrl(URL_INITIAL_EXPRESSION).value as Expression).expression)
                    }
                }
                //Creating parameterObject to pass encounterId
                val parameterObject = Parameters().apply {
                    parameter = listOf(
                        Parameters.ParametersParameterComponent().apply {
                            name = "encounterid"
                            value = StringType(encounterId)
                        }
                    )
                }
                //Evaluating Library
                val parameters = fhirOperator.evaluateLibrary(
                    cqlLibraryURL,
                    patientId,
                    parameterObject,
                    expressionSet
                ) as Parameters
                //Inject parameters to appropriate places
                questionnaire.item.forEach { item ->
                    if (item.hasExtension(URL_INITIAL_EXPRESSION)) {
                        val value =
                            parameters.getParameter((item.getExtensionByUrl(URL_INITIAL_EXPRESSION).value as Expression).expression) //parameter has same name as linkId
                        //inject value in the QuestionnaireItem as InitialComponent
                        if (value != null)
                            item.initial =
                                mutableListOf(Questionnaire.QuestionnaireItemInitialComponent(value))
                    }
                }
            }
            return@withContext questionnaire
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}