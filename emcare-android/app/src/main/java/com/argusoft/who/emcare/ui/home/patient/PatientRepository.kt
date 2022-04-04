package com.argusoft.who.emcare.ui.home.patient

import android.app.Application
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.LOCATION_EXTENSION_URL
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.utils.extention.toPatientItem
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.search
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.*
import org.hl7.fhir.r4.utils.StructureMapUtilities
import java.util.*
import javax.inject.Inject

class PatientRepository @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val application: Application
) {

    fun getQuestionnaire(questionnaireId: String) = flow {
        emit(ApiResponse.Success(data = fhirEngine.load(Questionnaire::class.java, questionnaireId)))
    }

    fun getPatients(search: String? = null, locationId: Int?) = flow {
        val riskAssessment = getRiskAssessments()
        val list = fhirEngine.search<Patient> {
            if (!search.isNullOrEmpty())
                filter(
                    Patient.NAME,
                    {
                        modifier = StringFilterModifier.CONTAINS
                        value = search
                    }
                )
            sort(Patient.GIVEN, Order.ASCENDING)
            count = 100
            from = 0
        }.filter {
            (it.getExtensionByUrl(LOCATION_EXTENSION_URL)?.value as? Identifier)?.value == locationId.toString()
        }.mapIndexed { index, fhirPatient ->
            fhirPatient.toPatientItem(index + 1, riskAssessment)
        }
        emit(ApiResponse.Success(data = list))
    }

    private suspend fun getRiskAssessments(): Map<String, RiskAssessment?> {
        return fhirEngine.search<RiskAssessment> {}.groupBy { it.subject.reference }.mapValues { entry ->
            entry
                .value
                .filter { it.hasOccurrence() }.maxByOrNull { it.occurrenceDateTimeType.value }
        }
    }

    fun getPatientDetails(patientId: String?) = flow {
        if (patientId != null) {
            emit(ApiResponse.Success(fhirEngine.load(Patient::class.java, patientId).convertPatientToPatientItem()))
        }
    }



    fun savePatient(questionnaireResponse: QuestionnaireResponse, questionnaire: String, locationId: Int) = flow {
        val questionnaireResource: Questionnaire = FhirContext.forR4().newJsonParser().parseResource(questionnaire) as Questionnaire
        val mapping =
            """
            map 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureMap/structuremap-emcare-patient-emcarea.registration.p' = 'emcare-patient-EmCareA.Registration.P'
            
            uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Questionnaire/questionnaire-emcarea.registration.p' alias 'emcarea.registration.p' as source
            uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Profile/profile-emcare-patient' alias 'emcare-patient' as target
            group  main(
                source qr : 'emcarea.registration.p',
                target tgt : 'emcare-patient'
            ) {
                qr.item as item then {
                    item.answer first as a where item.linkId = 'EmCare.A.DE01'  then { tgt.identifier as id then {  a -> id.type = 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/CodeSystem/codesystem-emcare-custom-codes', id.value = a 'uid-1';} 'EmCare.A.DE01-1'; } 'EmCare.A.DE01';
                    item.answer first as a where item.linkId = 'EmCare.A.DE02'  then { tgt.identifier as id then {  a -> id.type = 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/CodeSystem/codesystem-emcare-custom-codes', id.value = a 'uid-1';} 'EmCare.A.DE02-1'; } 'EmCare.A.DE02';
                    item.answer first as a where item.linkId = 'EmCare.A.DE03'  then { tgt.identifier as id then {  a -> id.type = 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/CodeSystem/codesystem-emcare-custom-codes' 'noid';} 'EmCare.A.DE03-1'; } 'EmCare.A.DE03';
                    item.answer first as a where item.linkId = 'EmCare.A.DE04'  then { tgt.name as name then {  a -> name.given = create(a) 'set-given-0';} 'EmCare.A.DE04-1'; } 'EmCare.A.DE04';
                    item.answer first as a where item.linkId = 'EmCare.A.DE05'  then { tgt.name as name then {  a -> name.given= create(a) 'set-given-1';} 'EmCare.A.DE05-1'; } 'EmCare.A.DE05';
                    item.answer first as a where item.linkId = 'EmCare.A.DE06'  then { tgt.name as name then {  a -> name.family = a 'set-family';} 'EmCare.A.DE06-1'; } 'EmCare.A.DE06';
                    item.answer first as a where item.linkId = 'EmCare.A.DE08'  then { a -> tgt.birthDate = a 'EmCare.A.DE08-1'; } 'EmCare.A.DE08';
                    item.answer first as a where item.linkId = 'EmCare.A.DE09'  then { tgt.extension as ext where ext.url ='https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Extension/birthTime' as ext then {a -> ext.valueTime = a 'set-time';} 'EmCare.A.DE09-1'; } 'EmCare.A.DE09';
                    item.answer first as a where item.linkId = 'EmCare.A.DE12'  then { tgt.extension as ext where ext.url ='https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Extension/birthDateEstimator' as ext then {a -> ext.valueCode = a 'set-code';} 'EmCare.A.DE12-1'; } 'EmCare.A.DE12';
                    item.answer first as a where item.linkId = 'EmCare.A.DE16'  then { a -> tgt.gender = translate(a, 'sex-of-the-client', 'http://hl7.org/fhir/administrative-gender') 'EmCare.A.DE16-1'; } 'EmCare.A.DE16';
                    item.answer first as a where item.linkId = 'EmCare.A.DE20'  then { tgt.address as address then { a -> address.text = a "set-address";} 'EmCare.A.DE20-1'; } 'EmCare.A.DE20';
                    item.answer first as a where item.linkId = 'EmCare.A.DE47'  then { tgt.extension as ext where ext.url ='https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Extension/caregiver' as ext then {a -> ext.valueReference= a 'set-ref';} 'EmCare.A.DE47-1'; } 'EmCare.A.DE47';
                    item.answer first as a where item.linkId = 'EmCare.A.DE31'  then { tgt.extension as ext where ext.url ='https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Extension/motherVitalStatus' as ext then {a -> ext.valueCode = a 'set-code';} 'EmCare.A.DE31-1'; } 'EmCare.A.DE31';
                    item.answer first as a where item.linkId = 'EmCare.A.DE32'  then { tgt.extension as ext where ext.url ='https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Extension/fatherVitalStatus' as ext then {a -> ext.valueCode = a 'set-code';} 'EmCare.A.DE32-1'; } 'EmCare.A.DE32';
                } 'itemsemcarea.registration.p-emcare-patient';
            } 
            """
        val entry = ResourceMapper.extract(application, questionnaireResource, questionnaireResponse) {
                _,
                worker ->
            StructureMapUtilities(worker).parse(mapping, "")
        }.entryFirstRep
        if (entry.resource !is Patient) return@flow
        val patient = entry.resource as Patient
        if (patient.identifier.isNotEmpty()
        ) {
            emit(ApiResponse.Loading())
            //Adding id
            patient.id = UUID.randomUUID().toString()

            //Changing identifier value type from string to identifier object
            val patientIdentifier: Identifier = Identifier()
            patientIdentifier.use = Identifier.IdentifierUse.OFFICIAL
            patientIdentifier.value = questionnaireResponse.item[0].item[0].answerFirstRep.valueStringType.toString()
            patient.identifier = listOf(patientIdentifier)

            //Adding and saving caregiver details
            if (!questionnaireResponse.item[2].item[0].answer.isNullOrEmpty()) {
                val caregiver: RelatedPerson = RelatedPerson()
                caregiver.id = UUID.randomUUID().toString()
                val caregiverHumanName: HumanName = HumanName()
                caregiverHumanName.given = listOf(questionnaireResponse.item[2].item[0].answerFirstRep.valueStringType)
                if (!questionnaireResponse.item[2].item[1].answer.isNullOrEmpty()) {
                    caregiverHumanName.family = questionnaireResponse.item[2].item[1].answerFirstRep.valueStringType.toString()
                }
                caregiver.name = listOf(caregiverHumanName)

                //Saving Caregiver
                fhirEngine.save(caregiver)

                //adding caregiver reference to the patient
                val caregiverReference: Reference = Reference()
                val caregiverIdentifier: Identifier = Identifier()
                caregiverIdentifier.use = Identifier.IdentifierUse.OFFICIAL
                caregiverIdentifier.value = caregiver.id
                caregiver.identifier = listOf(caregiverIdentifier)
                caregiverReference.identifier = caregiverIdentifier
                val patientLinkComponent: Patient.PatientLinkComponent = Patient.PatientLinkComponent()
                patientLinkComponent.other = caregiverReference
                patient.link = listOf(patientLinkComponent)
            }
            //#Adding locationId
            val locationIdentifier = Identifier()
            locationIdentifier.use = Identifier.IdentifierUse.OFFICIAL
            locationIdentifier.value = locationId.toString()
            val extension: Extension = Extension()
                .setValue(locationIdentifier)
                .setUrl(LOCATION_EXTENSION_URL)
            patient.addExtension(extension)
            //End of location Id
            fhirEngine.save(patient)
            emit(ApiResponse.Success(1))
        }
    }

    fun deletePatient(patientId: String?) = flow {
        if (patientId != null) {
            fhirEngine.remove(Patient::class.java, patientId)
        }
        emit(ApiResponse.Success(1))
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