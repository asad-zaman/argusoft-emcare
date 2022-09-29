package com.argusoft.who.emcare.ui.home.patient

import android.app.Application
import com.argusoft.who.emcare.R
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import ca.uhn.fhir.rest.param.TokenParamModifier
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.LOCATION_EXTENSION_URL
import com.argusoft.who.emcare.ui.common.model.PatientItem
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
import com.google.android.fhir.search.filter.TokenFilterValue
import com.google.android.fhir.search.search
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.*
import org.hl7.fhir.r4.utils.StructureMapUtilities
import java.util.*
import javax.inject.Inject

class PatientRepository @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val application: Application
) {

    fun getQuestionnaire(questionnaireId: String) = flow {
        emit(ApiResponse.Success(data = fhirEngine.get<Questionnaire>(questionnaireId)))
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

    fun saveQuestionnaire(questionnaireResponse: QuestionnaireResponse, questionnaire: String, facilityId: String, patientId: String? = null, encounterId: String? = null) = flow {
        val questionnaireResource: Questionnaire = FhirContext.forR4().newJsonParser().parseResource(questionnaire) as Questionnaire
        val structureMapString = """map 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureMap/emcarea.registration.p' = 'emcarea.registration.p'
uses 'http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaireresponse' alias 'questionnaireResponse' as source
uses 'http://hl7.org/fhir/StructureDefinition/Bundle' alias 'Bundle' as target
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/patient' alias 'Patient' as target
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/communicationrequest' alias 'CommunicationRequest' as target
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcarepatient' alias 'EmCare Patient' as produced
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcarecommunicationrequest' alias 'EmCare CommunicationRequest' as produced
group bundletrans(source src : questionnaireResponse,target bundle : Bundle){
    src -> bundle.id = uuid() 'id';
    src -> bundle.type = 'batch' 'type';
    src -> bundle.entry as entry then {
        src.subject as subject then {
            subject.id as idval -> entry.request as request, request.method = 'PUT', request.url = append('/Patient/',idval) 'qosha';
        } 'vmunl';
        src -> entry.resource = create("Patient") as tgt then {
            src -> tgt then emcarepatient(src, tgt) 'wtkcj';
        } 'chrhz';
    } 'put-emcarepatient';
    src where src.item.where(linkId='EmCare.A.DE38').exists() and src.item.where(linkId='emcarecommunicationrequestid').first().answer.exists() then {
        src -> bundle.entry as entry, entry.request as request, request.method = 'POST', entry.resource = create('CommunicationRequest') as tgt then emcarecommunicationrequestemcareade38(src,tgt) 'act-EmCare.A.DE38';
    } 'emcarecommunicationrequest';
}

group emcarepatient(source src : questionnaireResponse,target tgt : Patient){
    src.item as item where linkId  = 'EmCare.A.DE01' then {
        item.answer first as a then {
            a.value as val -> tgt.identifier = create('Identifier' ) as identifier then {
                    val -> identifier.value = val,  identifier.use = 'official'    "id";
                } 'aemcareade01';
        } 'aemcareade01';
    } 'emcareade01';
    src.item as item where linkId  = 'EmCare.A.DE03' then {
        item.answer first as a -> tgt.identifier = create('Identifier' ) as identifier then {
                a -> identifier.value = 'EmCare.A.DE03',  identifier.system = 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/CodeSystem/emcare-custom-codes'  "noid";
            } 'aemcareade03';
    } 'emcareade03';
    src.item first as item  where linkId =  'EmCare.A.DE04' or linkId =  'EmCare.A.DE05' or linkId =  'EmCare.A.DE06' -> tgt as target,  target.name as name then SetOfficalGivenNameemcarepatient(src, name) 'emcareade04';
    src.item as item where linkId  = 'dob' then {
        item.answer first as a then {
            a.value as val -> tgt.birthDate = val 'adob';
        } 'adob';
    } 'dob';
    src.item as item where linkId  = 'EmCare.A.DE12' then {
        item.answer first as a then {
            a.value as val -> tgt.extension  = create('Extension') as ext ,  ext.url ='https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/birthDateEstimator',  ext.value = val 'aemcareade12';
        } 'aemcareade12';
    } 'emcareade12';
    src.item as item where linkId =  'EmCare.A.DE16' then { item.answer first as a then MapValueSetExtCodeemcareade16(a, tgt) 'emcareade16d'; } 'emcareade16';
    src.item as item where linkId  = 'EmCare.A.DE20' then {
        item.answer first as a then {
            a.value as val -> tgt.address as address, address.text = val 'aemcareade20';
        } 'aemcareade20';
    } 'emcareade20';
    src.item as item where linkId  = 'EmCare.A.DE48' then {
        item.answer first as a then {
            a.value as val -> tgt.extension = create('Extension') as ext ,  ext.url ='https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/caregiver', ext.value= val 'aemcareade48';
        } 'aemcareade48';
    } 'emcareade48';
}

group SetOfficalGivenNameemcarepatient(source src,target tgt){
    src -> tgt.use = 'official' then {
        src.item as item where linkId  =  'EmCare.A.DE04' then {
            item.answer first as a then {
                a.value as val -> tgt.given = val  'suvvu';
            } 'hitzl';
        } 'rlalt';
        src.item as item where linkId  =  'EmCare.A.DE05' then {
            item.answer first as a then {
                a.value as val -> tgt.given = val  'jowbj';
            } 'ddfpk';
        } 'dttue';
        src.item as item where linkId  =  'EmCare.A.DE06' then {
            item.answer first as a then {
                a.value as val -> tgt.family = val  'ilrsy';
            } 'qxjhg';
        } 'duayf';
    } 'ahyrg';
}

group MapValueSetExtCodeemcareade16(source src,target tgt){
    src -> tgt then {
        src -> tgt then {
            src where value.code = 'EmCare.A.DE17' -> tgt.gender = 'female' 'vqbpg';
            src where value.code = 'EmCare.A.DE18' -> tgt.gender = 'male' 'ivean';
            src where value.code = 'EmCare.A.DE19' -> tgt.gender = 'unknown' 'tsysl';
        } 'mapbase';
    } 'jbjjr';
}

group getIdemcarepatient(source src,target tgt){
    src.item first as item where linkId  =  'emcarepatientid' -> tgt then {
        item.answer first as a ->  tgt  then {
            a.value as val ->  tgt.id = val 'yrgho';
        } 'unidz';
    } 'hzxbs';
}

group getFullUrlemcarepatient(source src,target tgt){
    src.item first as item where linkId  =  'emcarepatientid' -> tgt then {
        item.answer first as a -> tgt then {
            a.value as val ->  tgt.fullUrl = append('urn:uuid:', val) 'ephwf';
        } 'kweoa';
    } 'txdhs';
}

group getUrlemcarepatient(source src,target tgt){
    src.item first as item where linkId  =  'emcarepatientid' -> tgt then {
        item.answer first as a ->  tgt then {
            a.value as val ->  ref.reference = append('/Patient/', val) 'qawig';
        } 'jdfhy';
    } 'zhmvf';
}

group emcarecommunicationrequestemcareade38(source src,target tgt){
    src ->  tgt.category = create('CodeableConcept') as cc, cc.coding = create('Coding') as c, c.system ='http://hl7.org/fhir/ValueSet/communication-category', c.code = 'notification' 'xfxga';
    src.questionnaire as q ->   tgt.about = create('Reference') as ref, ref.type ='Questionnaire', ref.reference = q 'quest';
    src.subject as subject ->   tgt.subject = subject  'bioep';
    src ->   tgt.recipient =create('Reference') as ref  then {
        src -> ref.type = 'RelatedPerson' 'okejf';
        src.item first as item where linkId  =  'emcarerelatedpersoncaregiveruuid' -> tgt then {
            item.answer first as a ->  tgt then {
                a.value as val ->  ref.reference = append('/RelatedPerson/', val) 'zbqnq';
            } 'dbhlt';
        } 'jeyxe';
    } 'gxyat';
}
"""
//        val structureMap = FhirContext.forR4().newJsonParser().parseResource(structureMapString) as StructureMap
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
            val extractedBundle = ResourceMapper.extract(
                questionnaireResource,
                questionnaireResponse,
                StructureMapExtractionContext(context = application.applicationContext) { _, worker -> //structureMap
                    StructureMapUtilities(worker).parse(structureMapString, "")
                }
            )

            if(patientId == null || encounterId == null){
                saveResourcesFromBundle(extractedBundle, questionnaireResponse.subject.identifier.value, questionnaireResponse.encounter.id, facilityId)
            } else {
                saveResourcesFromBundle(extractedBundle, patientId, encounterId, facilityId)
            }
            print("RESULT")
            print(FhirContext.forR4().newJsonParser().encodeResourceToString(extractedBundle))
            emit(ApiResponse.Success(1))
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