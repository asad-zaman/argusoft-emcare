package com.argusoft.who.emcare.ui.home.patient

import android.app.Application
import com.argusoft.who.emcare.R
import ca.uhn.fhir.context.FhirContext
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.LOCATION_EXTENSION_URL
import com.argusoft.who.emcare.ui.common.consultationFlowQuestionnaireList
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.ui.common.stageToQuestionnaireId
import com.argusoft.who.emcare.ui.home.ConsultationFlowRepository
import com.argusoft.who.emcare.utils.extention.toPatientItem
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.datacapture.mapping.StructureMapExtractionContext
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.android.fhir.delete
import com.google.android.fhir.get
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.search
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.*
import java.sql.Timestamp
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject

class PatientRepository @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val application: Application,
    private val consultationFlowRepository: ConsultationFlowRepository,
) {

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
            sort(Patient.GIVEN, Order.ASCENDING)
            count = 100
            from = 0
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


    /*
        To save the Questionnaire using structureMap
        1. Convert questionnaireResource from string to Fhir Object
        2. Fetch structuremap using id.
        3. extract resources with ResourceMapper.extract using structuremap
        4. Save the resources in bundle
        STEPS 5 and 6 to be done in viewmodel
        5. Generate Careplan using patientId, encounterId
        6. Do next step according to careplan
     */
    fun saveQuestionnaireFinal(questionnaireResponse: QuestionnaireResponse, questionnaire: String, patientId: String, encounterId: String, structureMapId: String, locationId: Int) = flow {

        val questionnaireResource: Questionnaire = FhirContext.forR4().newJsonParser().parseResource(questionnaire) as Questionnaire

        val structureMapResource = fhirEngine.get<StructureMap>(structureMapId)

        val extractedBundle = ResourceMapper.extract(
            questionnaireResource,
            questionnaireResponse,
            StructureMapExtractionContext(context = application.applicationContext) { _, _ -> structureMapResource
            }
        )

        val resourceSavedSuccessfully = saveResourcesFromBundle(extractedBundle, "", "","")
        if(!resourceSavedSuccessfully) {
            emit(ApiResponse.ApiError("Error saving resources"))
        }

        //Generate Careplan
        //Do next step must be done in viewModel

        emit(ApiResponse.Success(1))
    }

    fun saveQuestionnaire(questionnaireResponse: QuestionnaireResponse, questionnaire: String, facilityId: String, structureMapId: String = "", consultationFlowItemId: String? = null,consultationStage: String? = null) = flow {
        val parser = FhirContext.forR4().newJsonParser()
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

            saveResourcesFromBundle(extractedBundle, patientId, encounterId, facilityId)

            //update QuestionnarieResponse in currentConsultation and createNext Consultation
            if(consultationFlowItemId != null) {
                consultationFlowRepository.updateConsultationQuestionnaireResponseText(consultationFlowItemId, parser.encodeResourceToString(questionnaireResponse))
            } else {
                //Save first consultationFlowItem after creation
                consultationFlowRepository.saveConsultation(ConsultationFlowItem(
                    consultationStage = consultationStage,
                    patientId = patientId,
                    encounterId = encounterId,
                    questionnaireId = stageToQuestionnaireId[consultationStage],
                    structureMapId = stageToQuestionnaireId[consultationStage],
                    questionnaireResponseText = parser.encodeResourceToString(questionnaireResponse),
                    isActive = true,
                    consultationDate = ZonedDateTime.now(ZoneId.of("UTC")).toString().removeSuffix("Z[UTC]"),
                ))
            }

            //DUMMY LOGIC FOR NEXT CONSULTATION FOR NOW, TODO: Replace by Plan Definition
            val consultationStageIndex = consultationFlowQuestionnaireList.indexOf(consultationStage)
            if(consultationFlowQuestionnaireList.last().equals(consultationStage, true)){
                //End of consultation
                consultationFlowRepository.updateConsultationFlowInactiveByEncounterId(encounterId)
                emit(ApiResponse.Success(null))
            } else {
                val nextConsultationStage = consultationFlowQuestionnaireList.get(consultationStageIndex + 1)

                //create nextConsultationItem
                val nextConsultationFlowItem = ConsultationFlowItem(
                    consultationStage = nextConsultationStage,
                    patientId = patientId,
                    encounterId = encounterId,
                    questionnaireId = stageToQuestionnaireId[nextConsultationStage],
                    structureMapId = stageToQuestionnaireId[nextConsultationStage],
                    questionnaireResponseText = "",
                    isActive = true,
                    consultationDate = ZonedDateTime.now(ZoneId.of("UTC")).toString().removeSuffix("Z[UTC]"),
                )
                consultationFlowRepository.saveConsultation(nextConsultationFlowItem).collect{
                    emit(it)
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

    fun deletePatient(patientId: String?) = flow {
        if (patientId != null) {
            fhirEngine.delete<Patient>(patientId)
        }
        emit(ApiResponse.Success(1))
    }

    private suspend fun saveResourcesFromBundle(bundle: Bundle, patientId: String, encounterId: String, facilityId: String): Boolean {
        bundle.entry.forEach { entry ->
            if(entry.hasResource()){
                val resource = entry.resource
                when(resource.resourceType) {
                    ResourceType.Patient -> {
                        resource.id = patientId
                        (resource as Patient).addExtension(Extension().setValue(
                            Identifier().apply {
                                use = Identifier.IdentifierUse.OFFICIAL
                                value = facilityId
                            }
                        ).setUrl(LOCATION_EXTENSION_URL))
                    }
                    ResourceType.Encounter -> {
                        resource.id = encounterId
                    }
                    else -> {
                        resource.id = UUID.randomUUID().toString()
                    }
                }
                fhirEngine.create(resource)
            }
        }
        return true
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