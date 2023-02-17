package com.argusoft.who.emcare.ui.home.patient

import android.app.Application
import com.argusoft.who.emcare.R
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.ui.home.ConsultationFlowRepository
import com.argusoft.who.emcare.utils.extention.toPatientItem
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.datacapture.mapping.StructureMapExtractionContext
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.android.fhir.delete
import com.google.android.fhir.get
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.search
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.*
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject

class PatientRepository @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val application: Application,
    private val consultationFlowRepository: ConsultationFlowRepository,
    private val preference: Preference
) {

    private val Z_UTC = "Z[UTC]"

    fun getQuestionnaire(questionnaireId: String) = flow {
        emit(ApiResponse.Success(data = fhirEngine.get<Questionnaire>(questionnaireId)))
    }

    fun getPatientById(patientId: String) = flow {
        emit(ApiResponse.Success(data = fhirEngine.get<Patient>(patientId)))
    }

    fun getPatients(search: String? = null, facilityId: String?) = flow {
        val list = fhirEngine.search<Patient> {
            if (!search.isNullOrEmpty()) {
                filter(
                    Patient.NAME,
                    {
                        modifier = StringFilterModifier.CONTAINS
                        value = search
                    }
                )
                filter(
                    Patient.IDENTIFIER,
                    {
                        value = of(Identifier().apply { value = search })
                    }
                )
                operation = Operation.OR
            }
        }.filter {
            (it.getExtensionByUrl(LOCATION_EXTENSION_URL)?.value as? Identifier)?.value == facilityId
        }.mapIndexed { index, fhirPatient ->
            fhirPatient.toPatientItem(index + 1)
        }
        emit(ApiResponse.Success(data = list))
    }

    fun getPatientDetails(patientId: String?) = flow {
        if (patientId != null) {
            emit(ApiResponse.Success(fhirEngine.get<Patient>(patientId).convertPatientToPatientItem()))
        }
    }

    fun getPatient(patientId: String?) = flow {
        if (patientId != null) {
            emit(ApiResponse.Success(fhirEngine.get<Patient>(patientId)))
        }
    }

    fun saveQuestionnaire(questionnaireResponse: QuestionnaireResponse, questionnaire: String, facilityId: String, structureMapId: String = "", consultationFlowItemId: String? = null,consultationStage: String? = null) = flow {
        val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
        val questionnaireResource: Questionnaire = parser.parseResource(questionnaire) as Questionnaire
        try {
            if (QuestionnaireResponseValidator.validateQuestionnaireResponse(
                    questionnaireResource,
                    questionnaireResponse,
                    application
                )
                    .values
                    .flatten()
                    .any { it is Invalid }
            ) {
                throw InputMismatchException()
            }

            val structureMap = if (structureMapId.isEmpty()) fhirEngine.get<StructureMap>(questionnaireResource.id) else fhirEngine.get<StructureMap>(structureMapId)

            val patientId = questionnaireResponse.subject.identifier.value
            val encounterId = questionnaireResponse.encounter.id

            val extractedBundle = ResourceMapper.extract(
                questionnaireResource,
                questionnaireResponse,
                StructureMapExtractionContext(context = application.applicationContext) { _, _ -> structureMap
                }
            )
            saveResourcesFromBundle(extractedBundle, patientId, encounterId, facilityId, consultationFlowItemId)
            //update QuestionnarieResponse in currentConsultation and createNext Consultation
            if(consultationFlowItemId != null) {
                consultationFlowRepository.updateConsultationQuestionnaireResponseText(consultationFlowItemId, parser.encodeResourceToString(questionnaireResponse), ZonedDateTime.now(ZoneId.of("UTC")).toString().removeSuffix(Z_UTC))
            } else {
                //Save first consultationFlowItem after creation
                consultationFlowRepository.saveConsultation(ConsultationFlowItem(
                    consultationStage = consultationStage,
                    patientId = patientId,
                    encounterId = encounterId,
                    questionnaireId = stageToQuestionnaireId[consultationStage],
                    structureMapId = stageToStructureMapId[consultationStage],
                    questionnaireResponseText = parser.encodeResourceToString(questionnaireResponse),
                    isActive = true,
                    consultationDate = ZonedDateTime.now(ZoneId.of("UTC")).toString().removeSuffix(Z_UTC),
                ))
            }.collect {
                //Current implementation of stage order by questionnaire list
                val consultationStageIndex = consultationFlowStageList.indexOf(consultationStage)
                if(consultationFlowStageList.last().equals(consultationStage, true)){
                    //End of consultation
                    consultationFlowRepository.updateConsultationFlowInactiveByEncounterId(encounterId).collect {
                        emit(ApiResponse.Success(null))
                    }
                } else {
                    var exitConsultation = false

                    if(consultationStage.equals(CONSULTATION_STAGE_DANGER_SIGNS)) {
                        if(questionnaireResponse.hasItem()){
                            questionnaireResponse.item.forEach { item ->
                                if(item.linkId.equals(ASSESS_SICK_CHILD_LINK_ID) && item.hasAnswer()
                                    && item.answerFirstRep.valueCoding.code.equals(END_CONSULTATION_CODING_VALUE)) {
                                    exitConsultation = true

                                }
                            }
                        }
                    }
                    if(exitConsultation) {
                        consultationFlowRepository.updateConsultationFlowInactiveByEncounterId(encounterId).collect {
                            emit(ApiResponse.Success(null))
                        }
                    } else {
                        val nextConsultationStage = consultationFlowStageList[consultationStageIndex + 1]

                        //create nextConsultationItem
                        val nextConsultationFlowItem = ConsultationFlowItem(
                            consultationStage = nextConsultationStage,
                            patientId = patientId,
                            encounterId = encounterId,
                            questionnaireId = stageToQuestionnaireId[nextConsultationStage],
                            structureMapId = stageToStructureMapId[nextConsultationStage],
                            questionnaireResponseText = "",
                            isActive = true,
                            consultationDate = ZonedDateTime.now(ZoneId.of("UTC")).toString().removeSuffix(Z_UTC),
                        )
                        consultationFlowRepository.saveConsultation(nextConsultationFlowItem).collect{
                            emit(it)
                        }
                    }
                }
            }

        } catch(ex: InputMismatchException){
            emit(ApiResponse.ApiError(apiErrorMessageResId = R.string.error_valid_data))
        }
        catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.ApiError(apiErrorMessageResId = R.string.error_saving_resource))
        }
    }

    fun saveNewConsultation(questionnaireResponse: QuestionnaireResponse, consultationStage: String? = CONSULTATION_STAGE_REGISTRATION_ENCOUNTER) = flow {

        val patientId = questionnaireResponse.subject.identifier.value
        val encounterId = questionnaireResponse.encounter.id
        val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
        consultationFlowRepository.saveConsultation(ConsultationFlowItem(
            consultationStage = consultationStage,
            patientId = patientId,
            encounterId = encounterId,
            questionnaireId = stageToQuestionnaireId[consultationStage],
            structureMapId = stageToStructureMapId[consultationStage],
            questionnaireResponseText = parser.encodeResourceToString(questionnaireResponse),
            isActive = true,
            consultationDate = ZonedDateTime.now(ZoneId.of("UTC")).toString().removeSuffix(Z_UTC),
        )).collect {
            emit(it.data?.id)
        }
    }

    fun updateConsultationQuestionnaireResponse(consultationFlowItemId: String, questionnaireResponse: QuestionnaireResponse? = null) = flow {
        val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
        if(questionnaireResponse != null){
            consultationFlowRepository.updateConsultationQuestionnaireResponseText(consultationFlowItemId, parser.encodeResourceToString(questionnaireResponse), ZonedDateTime.now(ZoneId.of("UTC")).toString().removeSuffix(Z_UTC)).collect {
                emit(ApiResponse.Success(it.data))
            }
        } else {
            consultationFlowRepository.updateConsultationQuestionnaireResponseText(consultationFlowItemId, "", ZonedDateTime.now(ZoneId.of("UTC")).toString().removeSuffix(Z_UTC)).collect {
                emit(ApiResponse.Success(it.data))
            }
        }

    }

    /*
    Deletes observation of current and next consultations &
     deletes next consultations
     */
    fun deleteNextConsultations(consultationFlowItemId: String, encounterId: String) = flow {
        consultationFlowRepository.getNextConsultationFlowItemIds(consultationFlowItemId, encounterId).collect{ consultationFlowItemIdListResponse ->
            val consultationIds = mutableListOf(consultationFlowItemId)
            if(consultationFlowItemIdListResponse.data != null)
                consultationIds.addAll(consultationFlowItemIdListResponse.data)
            //Delete Next consultations
            consultationFlowRepository.deleteNextConsultations(consultationFlowItemId, encounterId).collect {
                deleteObservations(encounterId,consultationIds).apply {
                    emit(ApiResponse.Success("Deleted"))
                }
            }
        }
    }

    private suspend fun deleteObservations(encounterId: String, consultationFlowItemIdList: List<String>) {
        //Fetch observations using encounterId and after consultationDate
        val observations = fhirEngine.search<Observation> {
        }.filter { observation ->
            observation.encounter.id == encounterId
        }.filter { observation ->
            observation.hasNote() && consultationFlowItemIdList.contains(observation.noteFirstRep.text)
        }
        //delete the observations
        observations.forEach { observation ->
            fhirEngine.delete(ResourceType.Observation, observation.logicalId)
        }
    }

    fun deletePatient(patientId: String?) = flow {
        if (patientId != null) {
            fhirEngine.delete<Patient>(patientId)
        }
        emit(ApiResponse.Success(1))
    }

    private suspend fun saveResourcesFromBundle(bundle: Bundle, patientId: String, encounterId: String, facilityId: String, consultationFlowItemId: String?): Boolean {
        val clipboardBundle = preference.getSubmittedResource() ?: Bundle()
        bundle.entry.forEach { entry ->
            if(entry.hasResource()){
                val resource = entry.resource
                when(resource.resourceType) {
                    ResourceType.Patient -> {
                        //Setting id
                        resource.id = patientId
                        //Adding location extension
                        (resource as Patient).addExtension(Extension().setValue(
                            Identifier().apply {
                                use = Identifier.IdentifierUse.OFFICIAL
                                value = facilityId
                            }
                        ).setUrl(LOCATION_EXTENSION_URL))
                    }
                    ResourceType.RelatedPerson -> {
                        //setting id
                        resource.id = entry.request.url.substringAfterLast("/")
                    }
                    ResourceType.Encounter -> {
                        resource.id = encounterId
                    }
                    ResourceType.Observation -> {
                        resource.id = UUID.randomUUID().toString()
                        if(consultationFlowItemId != null) {
                            (resource as Observation).addNote(Annotation().apply {
                                text = consultationFlowItemId
                            })
                        }
                        (resource as Observation).issuedElement = InstantType.now()
                    }
                    else -> {
                        resource.id = UUID.randomUUID().toString()
                    }
                }
                clipboardBundle.addEntry(Bundle.BundleEntryComponent().setResource(resource))
                fhirEngine.create(resource)
            }
        }
        fhirEngine.get(ResourceType.Patient, patientId).apply {
            clipboardBundle.addEntry(Bundle.BundleEntryComponent().setResource(this))
            try {
                fhirEngine.get(ResourceType.Encounter, patientId).apply {
                    clipboardBundle.addEntry(Bundle.BundleEntryComponent().setResource(this))
                    preference.setSubmittedResource(clipboardBundle)
                    return true
                }
            } catch(e: Exception) {
                preference.setSubmittedResource(clipboardBundle)
                return true
            }
        }
    }

    private fun Patient.convertPatientToPatientItem(): PatientItem {

        val patientId = if (hasIdElement()) idElement.idPart else ""
        val name = if (hasName()) name[0].nameAsSingleString else ""
        val gender = if (hasGenderElement()) genderElement.valueAsString else ""
        val dob = if (hasBirthDateElement()) birthDateElement.valueAsString else ""
        val identifier = if (hasIdentifier()) identifier[0].value else ""
        val line = if (hasAddress() && address[0].line.isNotEmpty()) address[0].line[0].toString() else ""
        val city = if (hasAddress()) address[0].city else ""
        val country = if (hasAddress()) address[0].country else ""
        val isActive = active
        val html: String = if (hasText()) text.div.valueAsString else ""

        return PatientItem(
            id = patientId,
            name = name,
            gender = gender,
            dob = dob,
            identifier = identifier,
            line = line,
            city = city,
            country = country,
            isActive = isActive,
            html = html
        )

    }
}