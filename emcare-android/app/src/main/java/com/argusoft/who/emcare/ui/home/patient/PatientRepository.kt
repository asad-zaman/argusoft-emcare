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
import com.google.android.fhir.datacapture.mapping.StructureMapExtractionContext
import com.google.android.fhir.delete
import com.google.android.fhir.get
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.StringFilterModifier
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
            (it.getExtensionByUrl(LOCATION_EXTENSION_URL)?.value as? Identifier)?.value == facilityId
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

        val resourceSavedSuccessfully = saveResourcesFromBundle(extractedBundle)
        if(!resourceSavedSuccessfully) {
            emit(ApiResponse.ApiError("Error saving resources"))
        }

        //Generate Careplan
        //Do next step must be done in viewModel

        emit(ApiResponse.Success(1))
    }

    fun saveQuestionnaire(questionnaireResponse: QuestionnaireResponse, questionnaire: String, patientId: String, structureMap: String?, locationId: Int) = flow {
        val questionnaireResource: Questionnaire = FhirContext.forR4().newJsonParser().parseResource(questionnaire) as Questionnaire
        val entry = ResourceMapper.extract(
            questionnaireResource,
            questionnaireResponse,
            StructureMapExtractionContext(context = application) { _, worker ->
                StructureMapUtilities(worker).parse(structureMap, "")
            },
        )
        //TODO: save resource using structuremap.
        emit(ApiResponse.Success(1))
    }

    fun savePatient(questionnaireResponse: QuestionnaireResponse, questionnaire: String, locationId: Int) = flow {
        val questionnaireResource: Questionnaire = FhirContext.forR4().newJsonParser().parseResource(questionnaire) as Questionnaire
        val structureMapString = """map 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureMap/emcarea.registration.p' = 'emcarea.registration.p'
uses 'http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaireresponse' alias 'questionnaireResponse' as source
uses 'http://hl7.org/fhir/StructureDefinition/Bundle' alias 'Bundle' as target
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/patient' alias 'Patient' as target
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/encounter' alias 'Encounter' as target
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/relatedperson' alias 'RelatedPerson' as target
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/communicationrequest' alias 'CommunicationRequest' as target
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcarepatient' alias 'EmCare Patient' as produced
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcareencounter' alias 'EmCare Encounter' as produced
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcarerelatedpersoncaregiver' alias 'EmCare RelatedPerson Caregiver' as produced
uses 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcarecommunicationrequest' alias 'EmCare CommunicationRequest' as produced
group bundletrans(source src : questionnaireResponse,target bundle : Bundle){
    src -> bundle.id = uuid() 'id';
    src -> bundle.type = 'batch' 'type';
    src -> bundle.entry as entry then {
        src.subject as subject then {
            subject.id as idval -> entry.request as request, request.method = 'PUT', request.url = append('/Patient/',idval) 'lbmkr';
        } 'owzfk';
        src -> entry.resource = create("Patient") as tgt then {
            src -> tgt then emcarepatient(src, tgt) 'pygvq';
            src.subject as subject then {
                subject.id as idval  -> tgt.id = idval 'uyhep';
            } 'uuxca';
        } 'shzvz';
    } 'put-emcarepatient';
    src -> bundle.entry as entry then {
        src.encounter as encounter then {
            encounter.id as idval  -> entry.request as request, request.method = 'PUT', request.url = append('/Encounter/',idval) 'azich';
        } 'iqxck';
        src -> entry.resource = create("Encounter") as tgt then {
            src -> tgt then emcareencounter(src, tgt) 'ltpli';
            src.encounter as encounter then {
                encounter.identifier as idval-> tgt.id = idval 'flckx';
            } 'oegwb';
            src.subject as sub -> tgt.subject = sub 'dnxfm';
        } 'ohyoj';
    } 'put-emcareencounter';
    src where src.item.where(linkId='emcarerelatedpersoncaregiverid').answer.exists()-> bundle.entry as entry then {
        src.item first as item where linkId  =  'emcarerelatedpersoncaregiverid' -> entry.request as request, request.method = 'PUT' then {
            item.answer first as a ->  request then {
                a.value as val ->  request.url = append('/RelatedPerson/', val) 'gvupk';
            } 'farat';
        } 'uuegw';
        src -> entry.resource = create("RelatedPerson") as tgt then {
            src -> tgt then emcarerelatedpersoncaregiver(src, tgt) 'lufdn';
            src -> entry then getIdemcarerelatedpersoncaregiver(src, tgt) 'eppzc';
        } 'lqanj';
    } 'put-emcarerelatedpersoncaregiver';
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
    src.item first as item  where linkId =  'EmCare.A.DE04'  or linkId =  'EmCare.A.DE05' or linkId =  'EmCare.A.DE06' -> tgt as target,  target.name as name then SetOfficalGivenNameemcarepatient(src, name) 'emcareade04';
    src.item as item where linkId  = 'EmCare.A.DE12' then {
        item.answer first as a then {
            a.value as val -> tgt.extension  = create('Extension') as ext ,  ext.url ='https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/birthDateEstimator',  ext.value = val 'aemcareade12';
        } 'aemcareade12';
    } 'emcareade12';
    src.item as item where linkId  = 'dob' then {
        item.answer first as a then {
            a.value as val -> tgt.birthDate = val 'adob';
        } 'adob';
    } 'dob';
    src.item as item where linkId =  'EmCare.A.DE16' then { item.answer first as a then MapValueSetExtCodeemcareade16(a, tgt) 'emcareade16d'; } 'emcareade16';
    src.item as item where linkId  = 'EmCare.A.DE20' then {
        item.answer first as a then {
            a.value as val -> tgt.address as address, address.text = val 'aemcareade20';
        } 'aemcareade20';
    } 'emcareade20';
    src.item as item where linkId  = 'EmCare.A.DE47' then {
        item.answer first as a then {
            a.value as val -> tgt.extension = create('Extension') as ext ,  ext.url ='https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/caregiver', ext.value= val 'aemcareade47';
        } 'aemcareade47';
    } 'emcareade47';
    src.item as item where linkId  = 'EmCare.A.DE31' then {
        item.answer first as a then {
            a.value as val -> tgt.extension = create('Extension') as ext ,  ext.url ='https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Extension/motherVitalStatus', ext.value= val 'aemcareade31';
        } 'aemcareade31';
    } 'emcareade31';
    src.item as item where linkId  = 'EmCare.A.DE32' then {
        item.answer first as a then {
            a.value as val -> tgt.extension = create('Extension') as ext ,  ext.url ='https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Extension/fatherVitalStatus', ext.value= val 'aemcareade32';
        } 'aemcareade32';
    } 'emcareade32';
}

group SetOfficalGivenNameemcarepatient(source src,target tgt){
    src -> tgt.use = 'official' then {
        src.item as item where linkId  =  'EmCare.A.DE04'  -> tgt then {item.answer as a -> tgt.given = a 'f';} 'weafv';
        src.item as item where linkId  =  'EmCare.A.DE05' -> tgt then {item.answer as a -> tgt.given = a 'f';} 'vrmuc';
        src.item as item where linkId  =  'EmCare.A.DE06' -> tgt then {item.answer as a -> tgt.family = a 'f';} 'onshu';
    } 'pxrbd';
}

group MapValueSetExtCodeemcareade16(source src,target tgt){
    src -> tgt then {
        src -> tgt then {
            src where value.code = 'EmCare.A.DE17' -> tgt.gender = 'female' 'ignfc';
            src where value.code = 'EmCare.A.DE18' -> tgt.gender = 'male' 'gqbeq';
            src where value.code = 'EmCare.A.DE19' -> tgt.gender = 'unknown' 'pivse';
        } 'mapbase';
    } 'qioaj';
}

group getIdemcarepatient(source src,target tgt){
    src.item first as item where linkId  =  'emcarepatientid' -> tgt then {
        item.answer first as a ->  tgt  then {
            a.value as val ->  tgt.id = val 'snqet';
        } 'zzexo';
    } 'fyoti';
}

group getFullUrlemcarepatient(source src,target tgt){
    src.item first as item where linkId  =  'emcarepatientid' -> tgt then {
        item.answer first as a -> tgt then {
            a.value as val ->  tgt.fullUrl = append('urn:uuid:', val) 'fiyif';
        } 'rxioy';
    } 'uqsir';
}

group getUrlemcarepatient(source src,target tgt){
    src.item first as item where linkId  =  'emcarepatientid' -> tgt then {
        item.answer first as a ->  tgt then {
            a.value as val ->  ref.reference = append('/Patient/', val) 'gnmae';
        } 'svggh';
    } 'gjewf';
}

group emcareencounter(source src : questionnaireResponse,target tgt : Encounter){
    src.item as item where linkId  = 'EmCare.A.DE07' then {
        item.answer first as a then {
            a.value as val -> tgt.period as period , period.start = val 'aemcareade07';
        } 'aemcareade07';
    } 'emcareade07';
}

group getIdemcareencounter(source src,target tgt){
    src.item first as item where linkId  =  'emcareencounterid' -> tgt then {
        item.answer first as a ->  tgt  then {
            a.value as val ->  tgt.id = val 'ojgon';
        } 'ivadz';
    } 'lxdey';
}

group getFullUrlemcareencounter(source src,target tgt){
    src.item first as item where linkId  =  'emcareencounterid' -> tgt then {
        item.answer first as a -> tgt then {
            a.value as val ->  tgt.fullUrl = append('urn:uuid:', val) 'yxipo';
        } 'nkeqi';
    } 'ciqty';
}

group getUrlemcareencounter(source src,target tgt){
    src.item first as item where linkId  =  'emcareencounterid' -> tgt then {
        item.answer first as a ->  tgt then {
            a.value as val ->  ref.reference = append('/Encounter/', val) 'tamec';
        } 'pvzox';
    } 'spkkq';
}

group emcarerelatedpersoncaregiver(source src : questionnaireResponse,target tgt : RelatedPerson){
    src.item first as item  where linkId =  'EmCare.A.DE21'  or linkId =  'EmCare.A.DE22' or linkId =  'EmCare.A.DE23' -> tgt as target,  target.name as name then SetOfficalGivenNameemcarerelatedpersoncaregiver(src, name) 'emcareade21';
    src.item as item where linkId  = 'EmCare.A.DE35' then {
        item.answer first as a then {
            a.value as val -> tgt.telecom as tel, tel.system = 'phone', tel.use ='mobile', tel.value = val 'aemcareade35';
        } 'aemcareade35';
    } 'emcareade35';
    src.item as item where linkId  = 'EmCare.A.DE36' then {
        item.answer first as a then {
            a.value as val -> tgt.telecom as tel, tel.system = 'phone', tel.use ='home', tel.value = val 'aemcareade36';
        } 'aemcareade36';
    } 'emcareade36';
    src.item as item where linkId  = 'EmCare.A.DE37' then {
        item.answer first as a then {
            a.value as val -> tgt.telecom as tel, tel.system = 'phone', tel.use ='work', tel.value = val 'aemcareade37';
        } 'aemcareade37';
    } 'emcareade37';
}

group SetOfficalGivenNameemcarerelatedpersoncaregiver(source src,target tgt){
    src -> tgt.use = 'official' then {
        src.item as item where linkId  =  'EmCare.A.DE21'  -> tgt then {item.answer as a -> tgt.given = a 'f';} 'poqcl';
        src.item as item where linkId  =  'EmCare.A.DE22' -> tgt then {item.answer as a -> tgt.given = a 'f';} 'lfblv';
        src.item as item where linkId  =  'EmCare.A.DE23' -> tgt then {item.answer as a -> tgt.family = a 'f';} 'hanke';
    } 'vsqeb';
}

group getIdemcarerelatedpersoncaregiver(source src,target tgt){
    src.item first as item where linkId  =  'emcarerelatedpersoncaregiverid' -> tgt then {
        item.answer first as a ->  tgt  then {
            a.value as val ->  tgt.id = val 'jzzwg';
        } 'cibgz';
    } 'ictag';
}

group getFullUrlemcarerelatedpersoncaregiver(source src,target tgt){
    src.item first as item where linkId  =  'emcarerelatedpersoncaregiverid' -> tgt then {
        item.answer first as a -> tgt then {
            a.value as val ->  tgt.fullUrl = append('urn:uuid:', val) 'nnhqb';
        } 'jazjg';
    } 'frkca';
}

group getUrlemcarerelatedpersoncaregiver(source src,target tgt){
    src.item first as item where linkId  =  'emcarerelatedpersoncaregiverid' -> tgt then {
        item.answer first as a ->  tgt then {
            a.value as val ->  ref.reference = append('/RelatedPerson/', val) 'tbkgw';
        } 'ylnnj';
    } 'prrlm';
}

group emcarecommunicationrequestemcareade38(source src,target tgt){
    src ->  tgt.category = create('CodeableConcept') as cc, cc.coding = create('Coding') as c, c.system ='http://hl7.org/fhir/ValueSet/communication-category', c.code = 'notification' 'rebwn';
    src.questionnaire as q ->   tgt.about = create('Reference') as ref, ref.type ='Questionnaire', ref.reference = q 'quest';
    src.subject as subject ->   tgt.subject = subject  'dhkrz';
    src ->   tgt.recipient =create('Reference') as ref  then {
        src -> ref.type = 'RelatedPerson' 'endif';
        src.item first as item where linkId  =  'emcarerelatedpersoncaregiverid' -> tgt then {
            item.answer first as a ->  tgt then {
                a.value as val ->  ref.reference = append('/RelatedPerson/', val) 'urqkk';
            } 'cpgzw';
        } 'ghxhf';
    } 'hcrfi';
}
"""
//        val structureMap = FhirContext.forR4().newJsonParser().parseResource(structureMapString) as StructureMap
        val extractedBundle = ResourceMapper.extract(
            questionnaireResource,
            questionnaireResponse,
            StructureMapExtractionContext(context = application.applicationContext) { _, worker -> //structureMap
                StructureMapUtilities(worker).parse(structureMapString, "")
            }
        )
        saveResourcesFromBundle(extractedBundle)
        print("RESULT")
        print(FhirContext.forR4().newJsonParser().encodeResourceToString(extractedBundle))
        emit(ApiResponse.Success(1))

    }

    fun deletePatient(patientId: String?) = flow {
        if (patientId != null) {
            fhirEngine.delete<Patient>(patientId)
        }
        emit(ApiResponse.Success(1))
    }

    private suspend fun saveResourcesFromBundle(bundle: Bundle): Boolean {
        bundle.entry.forEach { entry ->
            if(entry.hasResource()){
                fhirEngine.create(entry.resource)
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