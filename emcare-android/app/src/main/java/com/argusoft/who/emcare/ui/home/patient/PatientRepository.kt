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
        val riskAssessment = getRiskAssessments()
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

        val resourceSavedSuccessfully = saveResourcesFromBundle(extractedBundle, "", "","")
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

    fun savePatient(questionnaireResponse: QuestionnaireResponse, questionnaire: String, facilityId: String) = flow {
        val questionnaireResource: Questionnaire = FhirContext.forR4().newJsonParser().parseResource(questionnaire) as Questionnaire
        val structureMapString = """{
                "resourceType": "StructureMap",
                "id": "emcarea.registration.p",
                "meta": {
                    "versionId": "438",
                    "lastUpdated": "2022-09-22T16:02:08.436000+00:00"
                },
                "text": {
                    "status": "generated",
                    "div": "<div xmlns=\"http://www.w3.org/1999/xhtml\"><pre>map &quot;https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureMap/emcarea.registration.p&quot; = &quot;emcarea.registration.p&quot;\r\n\r\n\r\nuses &quot;http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaireresponse&quot; alias 'questionnaireResponse' as source\r\nuses &quot;http://hl7.org/fhir/StructureDefinition/Bundle&quot; alias 'Bundle' as target\r\nuses &quot;https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/patient&quot; alias 'Patient' as target\r\nuses &quot;https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/relatedperson&quot; alias 'RelatedPerson' as target\r\nuses &quot;https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/communicationrequest&quot; alias 'CommunicationRequest' as target\r\nuses &quot;https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcarepatient&quot; alias 'EmCare Patient' as produced\r\nuses &quot;https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcarerelatedpersoncaregiver&quot; alias 'EmCare RelatedPerson Caregiver' as produced\r\nuses &quot;https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcarecommunicationrequest&quot; alias 'EmCare CommunicationRequest' as produced\r\n\r\ngroup bundletrans(source src : questionnaireResponse, target bundle : Bundle) {\r\n  src -&gt; bundle.id = uuid() &quot;id&quot;;\r\n  src -&gt; bundle.type = 'batch' &quot;type&quot;;\r\n  src -&gt; bundle.entry as entry then {\r\n    src.subject as subject then {\r\n      subject.id as idval -&gt;  entry.request as request,  request.method = 'PUT',  request.url = append('/Patient/', idval) &quot;nndcv&quot;;\r\n    } &quot;ffdic&quot;;\r\n    src -&gt; entry.resource = create('Patient') as tgt then {\r\n      src -&gt; tgt then emcarepatient(src, tgt) &quot;qcrdl&quot;;\r\n      src.subject as subject then {\r\n        subject.id as idval -&gt; tgt.id = idval &quot;ssdso&quot;;\r\n      } &quot;fcuhp&quot;;\r\n    } &quot;tcljk&quot;;\r\n  } &quot;put-emcarepatient&quot;;\r\n  src where src.item.where(linkId = 'emcarerelatedpersoncaregiverid').answer.exists() -&gt; bundle.entry as entry then {\r\n    src.item first as item where linkId = 'emcarerelatedpersoncaregiverid' -&gt;  entry.request as request,  request.method = 'PUT' then {\r\n      item.answer first as a -&gt; request then {\r\n        a.value as val -&gt; request.url = append('/RelatedPerson/', val) &quot;ivbcn&quot;;\r\n      } &quot;dhqkq&quot;;\r\n    } &quot;ntudm&quot;;\r\n    src -&gt; entry.resource = create('RelatedPerson') as tgt then {\r\n      src -&gt; tgt then emcarerelatedpersoncaregiver(src, tgt) &quot;wvafz&quot;;\r\n      src -&gt; entry then getIdemcarerelatedpersoncaregiver(src, tgt) &quot;igezz&quot;;\r\n    } &quot;hzfzg&quot;;\r\n  } &quot;put-emcarerelatedpersoncaregiver&quot;;\r\n  src where src.item.where(linkId = 'EmCare.A.DE38').exists() and src.item.where(linkId = 'emcarecommunicationrequestid').first().answer.exists() then {\r\n    src -&gt;  bundle.entry as entry,  entry.request as request,  request.method = 'POST',  entry.resource = create('CommunicationRequest') as tgt then emcarecommunicationrequestemcareade38(src, tgt) &quot;DE38&quot;;\r\n  } &quot;emcarecommunicationrequest&quot;;\r\n}\r\n\r\ngroup emcarepatient(source src : questionnaireResponse, target tgt : Patient) {\r\n  src.item as item where linkId = 'EmCare.A.DE01' then {\r\n    item.answer first as a then {\r\n      a.value as val -&gt; tgt.identifier = create('Identifier') as identifier then {\r\n        val -&gt;  identifier.value = val,  identifier.use = 'official' &quot;id&quot;;\r\n      } &quot;aemcareade01&quot;;\r\n    } &quot;aemcareade01&quot;;\r\n  } &quot;emcareade01&quot;;\r\n  src.item as item where linkId = 'EmCare.A.DE03' then {\r\n    item.answer first as a -&gt; tgt.identifier = create('Identifier') as identifier then {\r\n      a -&gt;  identifier.value = 'EmCare.A.DE03',  identifier.system = 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/CodeSystem/emcare-custom-codes' &quot;noid&quot;;\r\n    } &quot;aemcareade03&quot;;\r\n  } &quot;emcareade03&quot;;\r\n  src.item first as item where (linkId = 'EmCare.A.DE04') or (linkId = 'EmCare.A.DE05') or (linkId = 'EmCare.A.DE06') -&gt;  tgt as target,  target.name as name then SetOfficalGivenNameemcarepatient(src, name) &quot;emcareade04&quot;;\r\n  src.item as item where linkId = 'EmCare.A.DE12' then {\r\n    item.answer first as a then {\r\n      a.value as val -&gt;  tgt.extension = create('Extension') as ext,  ext.url = 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/birthDateEstimator',  ext.value = val &quot;aemcareade12&quot;;\r\n    } &quot;aemcareade12&quot;;\r\n  } &quot;emcareade12&quot;;\r\n  src.item as item where linkId = 'EmCare.A.DE16' then {\r\n    item.answer first as a then MapValueSetExtCodeemcareade16(a, tgt) &quot;emcareade16d&quot;;\r\n  } &quot;emcareade16&quot;;\r\n  src.item as item where linkId = 'EmCare.A.DE20' then {\r\n    item.answer first as a then {\r\n      a.value as val -&gt;  tgt.address as address,  address.text = val &quot;aemcareade20&quot;;\r\n    } &quot;aemcareade20&quot;;\r\n  } &quot;emcareade20&quot;;\r\n  src.item as item where linkId = 'EmCare.A.DE47' then {\r\n    item.answer first as a then {\r\n      a.value as val -&gt;  tgt.extension = create('Extension') as ext,  ext.url = 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/caregiver',  ext.value = val &quot;aemcareade47&quot;;\r\n    } &quot;aemcareade47&quot;;\r\n  } &quot;emcareade47&quot;;\r\n  src.item as item where linkId = 'EmCare.A.DE31' then {\r\n    item.answer first as a then {\r\n      a.value as val -&gt;  tgt.extension = create('Extension') as ext,  ext.url = 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Extension/motherVitalStatus',  ext.value = val &quot;aemcareade31&quot;;\r\n    } &quot;aemcareade31&quot;;\r\n  } &quot;emcareade31&quot;;\r\n  src.item as item where linkId = 'EmCare.A.DE32' then {\r\n    item.answer first as a then {\r\n      a.value as val -&gt;  tgt.extension = create('Extension') as ext,  ext.url = 'https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Extension/fatherVitalStatus',  ext.value = val &quot;aemcareade32&quot;;\r\n    } &quot;aemcareade32&quot;;\r\n  } &quot;emcareade32&quot;;\r\n}\r\n\r\ngroup SetOfficalGivenNameemcarepatient(source src, target tgt) {\r\n  src -&gt; tgt.use = 'official' then {\r\n    src.item as item where linkId = EmCare.A.DE04 then {\r\n      item.answer first as a then {\r\n        a.value as val -&gt; tgt.given = val &quot;rsitd&quot;;\r\n      } &quot;hjads&quot;;\r\n    } &quot;yrogw&quot;;\r\n    src.item as item where linkId = EmCare.A.DE05 then {\r\n      item.answer first as a then {\r\n        a.value as val -&gt; tgt.given = val &quot;qutpp&quot;;\r\n      } &quot;ysjid&quot;;\r\n    } &quot;hyvkw&quot;;\r\n    src.item as item where linkId = EmCare.A.DE06 then {\r\n      item.answer first as a then {\r\n        a.value as val -&gt; tgt.family = val &quot;nyuyd&quot;;\r\n      } &quot;gbeml&quot;;\r\n    } &quot;hmuyh&quot;;\r\n  } &quot;lkeex&quot;;\r\n}\r\n\r\ngroup MapValueSetExtCodeemcareade16(source src, target tgt) {\r\n  src -&gt; tgt then {\r\n    src -&gt; tgt then {\r\n      src where value.code = 'EmCare.A.DE17' -&gt; tgt.gender = 'female' &quot;jrieo&quot;;\r\n      src where value.code = 'EmCare.A.DE18' -&gt; tgt.gender = 'male' &quot;ysukh&quot;;\r\n      src where value.code = 'EmCare.A.DE19' -&gt; tgt.gender = 'unknown' &quot;fbcgu&quot;;\r\n    } &quot;mapbase&quot;;\r\n  } &quot;ndkeo&quot;;\r\n}\r\n\r\ngroup getIdemcarepatient(source src, target tgt) {\r\n  src.item first as item where linkId = 'emcarepatientid' -&gt; tgt then {\r\n    item.answer first as a -&gt; tgt then {\r\n      a.value as val -&gt; tgt.id = val &quot;mhhco&quot;;\r\n    } &quot;hunkz&quot;;\r\n  } &quot;vsgnd&quot;;\r\n}\r\n\r\ngroup getFullUrlemcarepatient(source src, target tgt) {\r\n  src.item first as item where linkId = 'emcarepatientid' -&gt; tgt then {\r\n    item.answer first as a -&gt; tgt then {\r\n      a.value as val -&gt; tgt.fullUrl = append('urn:uuid:', val) &quot;wjzds&quot;;\r\n    } &quot;cfzjo&quot;;\r\n  } &quot;blgdd&quot;;\r\n}\r\n\r\ngroup getUrlemcarepatient(source src, target tgt) {\r\n  src.item first as item where linkId = 'emcarepatientid' -&gt; tgt then {\r\n    item.answer first as a -&gt; tgt then {\r\n      a.value as val -&gt; ref.reference = append('/Patient/', val) &quot;xgxxa&quot;;\r\n    } &quot;ljeyg&quot;;\r\n  } &quot;wdosi&quot;;\r\n}\r\n\r\ngroup emcarerelatedpersoncaregiver(source src : questionnaireResponse, target tgt : RelatedPerson) {\r\n  src.item first as item where (linkId = 'EmCare.A.DE21') or (linkId = 'EmCare.A.DE22') or (linkId = 'EmCare.A.DE23') -&gt;  tgt as target,  target.name as name then SetOfficalGivenNameemcarerelatedpersoncaregiver(src, name) &quot;emcareade21&quot;;\r\n  src.item as item where linkId = 'EmCare.A.DE35' then {\r\n    item.answer first as a then {\r\n      a.value as val -&gt;  tgt.telecom as tel,  tel.system = 'phone',  tel.use = 'mobile',  tel.value = val &quot;aemcareade35&quot;;\r\n    } &quot;aemcareade35&quot;;\r\n  } &quot;emcareade35&quot;;\r\n  src.item as item where linkId = 'EmCare.A.DE36' then {\r\n    item.answer first as a then {\r\n      a.value as val -&gt;  tgt.telecom as tel,  tel.system = 'phone',  tel.use = 'home',  tel.value = val &quot;aemcareade36&quot;;\r\n    } &quot;aemcareade36&quot;;\r\n  } &quot;emcareade36&quot;;\r\n  src.item as item where linkId = 'EmCare.A.DE37' then {\r\n    item.answer first as a then {\r\n      a.value as val -&gt;  tgt.telecom as tel,  tel.system = 'phone',  tel.use = 'work',  tel.value = val &quot;aemcareade37&quot;;\r\n    } &quot;aemcareade37&quot;;\r\n  } &quot;emcareade37&quot;;\r\n  src.item as item where linkId = 'emcarerelatedpersoncaregiverid' then {\r\n    item.answer first as a -&gt; tgt then {\r\n      src.subject as sub -&gt; tgt.patient = sub &quot;patient&quot;;\r\n    } &quot;aemcarerelatedpersoncaregiverid&quot;;\r\n  } &quot;emcarerelatedpersoncaregiverid&quot;;\r\n}\r\n\r\ngroup SetOfficalGivenNameemcarerelatedpersoncaregiver(source src, target tgt) {\r\n  src -&gt; tgt.use = 'official' then {\r\n    src.item as item where linkId = EmCare.A.DE21 then {\r\n      item.answer first as a then {\r\n        a.value as val -&gt; tgt.given = val &quot;fjpoa&quot;;\r\n      } &quot;cknsv&quot;;\r\n    } &quot;lesds&quot;;\r\n    src.item as item where linkId = EmCare.A.DE22 then {\r\n      item.answer first as a then {\r\n        a.value as val -&gt; tgt.given = val &quot;depys&quot;;\r\n      } &quot;pklej&quot;;\r\n    } &quot;xsusg&quot;;\r\n    src.item as item where linkId = EmCare.A.DE23 then {\r\n      item.answer first as a then {\r\n        a.value as val -&gt; tgt.family = val &quot;vdbbh&quot;;\r\n      } &quot;npzuj&quot;;\r\n    } &quot;irrmx&quot;;\r\n  } &quot;wlaes&quot;;\r\n}\r\n\r\ngroup getIdemcarerelatedpersoncaregiver(source src, target tgt) {\r\n  src.item first as item where linkId = 'emcarerelatedpersoncaregiverid' -&gt; tgt then {\r\n    item.answer first as a -&gt; tgt then {\r\n      a.value as val -&gt; tgt.id = val &quot;kagpz&quot;;\r\n    } &quot;lnndz&quot;;\r\n  } &quot;qjgvm&quot;;\r\n}\r\n\r\ngroup getFullUrlemcarerelatedpersoncaregiver(source src, target tgt) {\r\n  src.item first as item where linkId = 'emcarerelatedpersoncaregiverid' -&gt; tgt then {\r\n    item.answer first as a -&gt; tgt then {\r\n      a.value as val -&gt; tgt.fullUrl = append('urn:uuid:', val) &quot;mripo&quot;;\r\n    } &quot;yiygw&quot;;\r\n  } &quot;fcofh&quot;;\r\n}\r\n\r\ngroup getUrlemcarerelatedpersoncaregiver(source src, target tgt) {\r\n  src.item first as item where linkId = 'emcarerelatedpersoncaregiverid' -&gt; tgt then {\r\n    item.answer first as a -&gt; tgt then {\r\n      a.value as val -&gt; ref.reference = append('/RelatedPerson/', val) &quot;aozdo&quot;;\r\n    } &quot;xutdy&quot;;\r\n  } &quot;ygqpk&quot;;\r\n}\r\n\r\ngroup emcarecommunicationrequestemcareade38(source src, target tgt) {\r\n  src -&gt;  tgt.category = create('CodeableConcept') as cc,  cc.coding = create('Coding') as c,  c.system = 'http://hl7.org/fhir/ValueSet/communication-category',  c.code = 'notification' &quot;jymjl&quot;;\r\n  src.questionnaire as q -&gt;  tgt.about = create('Reference') as ref,  ref.type = 'Questionnaire',  ref.reference = q &quot;quest&quot;;\r\n  src.subject as subject -&gt; tgt.subject = subject &quot;fmctx&quot;;\r\n  src -&gt; tgt.recipient = create('Reference') as ref then {\r\n    src -&gt; ref.type = 'RelatedPerson' &quot;ulytk&quot;;\r\n    src.item first as item where linkId = 'emcarerelatedpersoncaregiveruuid' -&gt; tgt then {\r\n      item.answer first as a -&gt; tgt then {\r\n        a.value as val -&gt; ref.reference = append('/RelatedPerson/', val) &quot;chfra&quot;;\r\n      } &quot;bswyu&quot;;\r\n    } &quot;sdulh&quot;;\r\n  } &quot;epnjz&quot;;\r\n}\r\n\r\n</pre></div>"
                },
                "url": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureMap/emcarea.registration.p",
                "name": "emcarea.registration.p",
                "status": "active",
                "structure": [
                    {
                        "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaireresponse",
                        "mode": "source",
                        "alias": "'questionnaireResponse'"
                    },
                    {
                        "url": "http://hl7.org/fhir/StructureDefinition/Bundle",
                        "mode": "target",
                        "alias": "'Bundle'"
                    },
                    {
                        "url": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/patient",
                        "mode": "target",
                        "alias": "'Patient'"
                    },
                    {
                        "url": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/relatedperson",
                        "mode": "target",
                        "alias": "'RelatedPerson'"
                    },
                    {
                        "url": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/communicationrequest",
                        "mode": "target",
                        "alias": "'CommunicationRequest'"
                    },
                    {
                        "url": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcarepatient",
                        "mode": "produced",
                        "alias": "'EmCare Patient'"
                    },
                    {
                        "url": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcarerelatedpersoncaregiver",
                        "mode": "produced",
                        "alias": "'EmCare RelatedPerson Caregiver'"
                    },
                    {
                        "url": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/emcarecommunicationrequest",
                        "mode": "produced",
                        "alias": "'EmCare CommunicationRequest'"
                    }
                ],
                "group": [
                    {
                        "name": "bundletrans",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "type": "questionnaireResponse",
                                "mode": "source"
                            },
                            {
                                "name": "bundle",
                                "type": "Bundle",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "id",
                                "source": [
                                    {
                                        "context": "src"
                                    }
                                ],
                                "target": [
                                    {
                                        "context": "bundle",
                                        "contextType": "variable",
                                        "element": "id",
                                        "transform": "uuid"
                                    }
                                ]
                            },
                            {
                                "name": "type",
                                "source": [
                                    {
                                        "context": "src"
                                    }
                                ],
                                "target": [
                                    {
                                        "context": "bundle",
                                        "contextType": "variable",
                                        "element": "type",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueString": "batch"
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "put-emcarepatient",
                                "source": [
                                    {
                                        "context": "src"
                                    }
                                ],
                                "target": [
                                    {
                                        "context": "bundle",
                                        "contextType": "variable",
                                        "element": "entry",
                                        "variable": "entry"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "ffdic",
                                        "source": [
                                            {
                                                "context": "src",
                                                "element": "subject",
                                                "variable": "subject"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "nndcv",
                                                "source": [
                                                    {
                                                        "context": "subject",
                                                        "element": "id",
                                                        "variable": "idval"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "entry",
                                                        "contextType": "variable",
                                                        "element": "request",
                                                        "variable": "request"
                                                    },
                                                    {
                                                        "context": "request",
                                                        "contextType": "variable",
                                                        "element": "method",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "PUT"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "request",
                                                        "contextType": "variable",
                                                        "element": "url",
                                                        "transform": "append",
                                                        "parameter": [
                                                            {
                                                                "valueString": "/Patient/"
                                                            },
                                                            {
                                                                "valueId": "idval"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    },
                                    {
                                        "name": "tcljk",
                                        "source": [
                                            {
                                                "context": "src"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "context": "entry",
                                                "contextType": "variable",
                                                "element": "resource",
                                                "variable": "tgt",
                                                "transform": "create",
                                                "parameter": [
                                                    {
                                                        "valueString": "Patient"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "qcrdl",
                                                "source": [
                                                    {
                                                        "context": "src"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "contextType": "variable",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "tgt"
                                                            }
                                                        ]
                                                    }
                                                ],
                                                "dependent": [
                                                    {
                                                        "name": "emcarepatient",
                                                        "variable": [
                                                            "src",
                                                            "tgt"
                                                        ]
                                                    }
                                                ]
                                            },
                                            {
                                                "name": "fcuhp",
                                                "source": [
                                                    {
                                                        "context": "src",
                                                        "element": "subject",
                                                        "variable": "subject"
                                                    }
                                                ],
                                                "rule": [
                                                    {
                                                        "name": "ssdso",
                                                        "source": [
                                                            {
                                                                "context": "subject",
                                                                "element": "id",
                                                                "variable": "idval"
                                                            }
                                                        ],
                                                        "target": [
                                                            {
                                                                "context": "tgt",
                                                                "contextType": "variable",
                                                                "element": "id",
                                                                "transform": "copy",
                                                                "parameter": [
                                                                    {
                                                                        "valueId": "idval"
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "put-emcarerelatedpersoncaregiver",
                                "source": [
                                    {
                                        "context": "src",
                                        "condition": "src.item.where(linkId = 'emcarerelatedpersoncaregiverid').answer.exists()"
                                    }
                                ],
                                "target": [
                                    {
                                        "context": "bundle",
                                        "contextType": "variable",
                                        "element": "entry",
                                        "variable": "entry"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "ntudm",
                                        "source": [
                                            {
                                                "context": "src",
                                                "element": "item",
                                                "listMode": "first",
                                                "variable": "item",
                                                "condition": "linkId = 'emcarerelatedpersoncaregiverid'"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "context": "entry",
                                                "contextType": "variable",
                                                "element": "request",
                                                "variable": "request"
                                            },
                                            {
                                                "context": "request",
                                                "contextType": "variable",
                                                "element": "method",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueString": "PUT"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "dhqkq",
                                                "source": [
                                                    {
                                                        "context": "item",
                                                        "element": "answer",
                                                        "listMode": "first",
                                                        "variable": "a"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "contextType": "variable",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "request"
                                                            }
                                                        ]
                                                    }
                                                ],
                                                "rule": [
                                                    {
                                                        "name": "ivbcn",
                                                        "source": [
                                                            {
                                                                "context": "a",
                                                                "element": "value",
                                                                "variable": "val"
                                                            }
                                                        ],
                                                        "target": [
                                                            {
                                                                "context": "request",
                                                                "contextType": "variable",
                                                                "element": "url",
                                                                "transform": "append",
                                                                "parameter": [
                                                                    {
                                                                        "valueString": "/RelatedPerson/"
                                                                    },
                                                                    {
                                                                        "valueId": "val"
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    },
                                    {
                                        "name": "hzfzg",
                                        "source": [
                                            {
                                                "context": "src"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "context": "entry",
                                                "contextType": "variable",
                                                "element": "resource",
                                                "variable": "tgt",
                                                "transform": "create",
                                                "parameter": [
                                                    {
                                                        "valueString": "RelatedPerson"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "wvafz",
                                                "source": [
                                                    {
                                                        "context": "src"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "contextType": "variable",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "tgt"
                                                            }
                                                        ]
                                                    }
                                                ],
                                                "dependent": [
                                                    {
                                                        "name": "emcarerelatedpersoncaregiver",
                                                        "variable": [
                                                            "src",
                                                            "tgt"
                                                        ]
                                                    }
                                                ]
                                            },
                                            {
                                                "name": "igezz",
                                                "source": [
                                                    {
                                                        "context": "src"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "contextType": "variable",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "entry"
                                                            }
                                                        ]
                                                    }
                                                ],
                                                "dependent": [
                                                    {
                                                        "name": "getIdemcarerelatedpersoncaregiver",
                                                        "variable": [
                                                            "src",
                                                            "tgt"
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcarecommunicationrequest",
                                "source": [
                                    {
                                        "context": "src",
                                        "condition": "src.item.where(linkId = 'EmCare.A.DE38').exists() and src.item.where(linkId = 'emcarecommunicationrequestid').first().answer.exists()"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "act-EmCare.A.DE38",
                                        "source": [
                                            {
                                                "context": "src"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "context": "bundle",
                                                "contextType": "variable",
                                                "element": "entry",
                                                "variable": "entry"
                                            },
                                            {
                                                "context": "entry",
                                                "contextType": "variable",
                                                "element": "request",
                                                "variable": "request"
                                            },
                                            {
                                                "context": "request",
                                                "contextType": "variable",
                                                "element": "method",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueString": "POST"
                                                    }
                                                ]
                                            },
                                            {
                                                "context": "entry",
                                                "contextType": "variable",
                                                "element": "resource",
                                                "variable": "tgt",
                                                "transform": "create",
                                                "parameter": [
                                                    {
                                                        "valueString": "CommunicationRequest"
                                                    }
                                                ]
                                            }
                                        ],
                                        "dependent": [
                                            {
                                                "name": "emcarecommunicationrequestemcareade38",
                                                "variable": [
                                                    "src",
                                                    "tgt"
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "emcarepatient",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "type": "questionnaireResponse",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "type": "Patient",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "emcareade01",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'EmCare.A.DE01'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "aemcareade01",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "aemcareade01",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "identifier",
                                                        "variable": "identifier",
                                                        "transform": "create",
                                                        "parameter": [
                                                            {
                                                                "valueString": "Identifier"
                                                            }
                                                        ]
                                                    }
                                                ],
                                                "rule": [
                                                    {
                                                        "name": "id",
                                                        "source": [
                                                            {
                                                                "context": "val"
                                                            }
                                                        ],
                                                        "target": [
                                                            {
                                                                "context": "identifier",
                                                                "contextType": "variable",
                                                                "element": "value",
                                                                "transform": "copy",
                                                                "parameter": [
                                                                    {
                                                                        "valueId": "val"
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "context": "identifier",
                                                                "contextType": "variable",
                                                                "element": "use",
                                                                "transform": "copy",
                                                                "parameter": [
                                                                    {
                                                                        "valueString": "official"
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcareade03",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'EmCare.A.DE03'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "aemcareade03",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "context": "tgt",
                                                "contextType": "variable",
                                                "element": "identifier",
                                                "variable": "identifier",
                                                "transform": "create",
                                                "parameter": [
                                                    {
                                                        "valueString": "Identifier"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "noid",
                                                "source": [
                                                    {
                                                        "context": "a"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "identifier",
                                                        "contextType": "variable",
                                                        "element": "value",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "EmCare.A.DE03"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "identifier",
                                                        "contextType": "variable",
                                                        "element": "system",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/CodeSystem/emcare-custom-codes"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcareade04",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "listMode": "first",
                                        "variable": "item",
                                        "condition": "(linkId = 'EmCare.A.DE04') or (linkId = 'EmCare.A.DE05') or (linkId = 'EmCare.A.DE06')"
                                    }
                                ],
                                "target": [
                                    {
                                        "contextType": "variable",
                                        "variable": "target",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueId": "tgt"
                                            }
                                        ]
                                    },
                                    {
                                        "context": "target",
                                        "contextType": "variable",
                                        "element": "name",
                                        "variable": "name"
                                    }
                                ],
                                "dependent": [
                                    {
                                        "name": "SetOfficalGivenNameemcarepatient",
                                        "variable": [
                                            "src",
                                            "name"
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcareade12",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'EmCare.A.DE12'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "aemcareade12",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "aemcareade12",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "extension",
                                                        "variable": "ext",
                                                        "transform": "create",
                                                        "parameter": [
                                                            {
                                                                "valueString": "Extension"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "ext",
                                                        "contextType": "variable",
                                                        "element": "url",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/birthDateEstimator"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "ext",
                                                        "contextType": "variable",
                                                        "element": "value",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcareade16",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'EmCare.A.DE16'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "emcareade16d",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "dependent": [
                                            {
                                                "name": "MapValueSetExtCodeemcareade16",
                                                "variable": [
                                                    "a",
                                                    "tgt"
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcareade20",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'EmCare.A.DE20'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "aemcareade20",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "aemcareade20",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "address",
                                                        "variable": "address"
                                                    },
                                                    {
                                                        "context": "address",
                                                        "contextType": "variable",
                                                        "element": "text",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcareade47",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'EmCare.A.DE47'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "aemcareade47",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "aemcareade47",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "extension",
                                                        "variable": "ext",
                                                        "transform": "create",
                                                        "parameter": [
                                                            {
                                                                "valueString": "Extension"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "ext",
                                                        "contextType": "variable",
                                                        "element": "url",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/StructureDefinition/caregiver"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "ext",
                                                        "contextType": "variable",
                                                        "element": "value",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcareade31",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'EmCare.A.DE31'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "aemcareade31",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "aemcareade31",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "extension",
                                                        "variable": "ext",
                                                        "transform": "create",
                                                        "parameter": [
                                                            {
                                                                "valueString": "Extension"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "ext",
                                                        "contextType": "variable",
                                                        "element": "url",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Extension/motherVitalStatus"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "ext",
                                                        "contextType": "variable",
                                                        "element": "value",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcareade32",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'EmCare.A.DE32'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "aemcareade32",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "aemcareade32",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "extension",
                                                        "variable": "ext",
                                                        "transform": "create",
                                                        "parameter": [
                                                            {
                                                                "valueString": "Extension"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "ext",
                                                        "contextType": "variable",
                                                        "element": "url",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "https://fhir.dk.swisstph-mis.ch/matchbox/fhir/Extension/fatherVitalStatus"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "ext",
                                                        "contextType": "variable",
                                                        "element": "value",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "SetOfficalGivenNameemcarepatient",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "lkeex",
                                "source": [
                                    {
                                        "context": "src"
                                    }
                                ],
                                "target": [
                                    {
                                        "context": "tgt",
                                        "contextType": "variable",
                                        "element": "use",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueString": "official"
                                            }
                                        ]
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "yrogw",
                                        "source": [
                                            {
                                                "context": "src",
                                                "element": "item",
                                                "variable": "item",
                                                "condition": "linkId = EmCare.A.DE04"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "hjads",
                                                "source": [
                                                    {
                                                        "context": "item",
                                                        "element": "answer",
                                                        "listMode": "first",
                                                        "variable": "a"
                                                    }
                                                ],
                                                "rule": [
                                                    {
                                                        "name": "rsitd",
                                                        "source": [
                                                            {
                                                                "context": "a",
                                                                "element": "value",
                                                                "variable": "val"
                                                            }
                                                        ],
                                                        "target": [
                                                            {
                                                                "context": "tgt",
                                                                "contextType": "variable",
                                                                "element": "given",
                                                                "transform": "copy",
                                                                "parameter": [
                                                                    {
                                                                        "valueId": "val"
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    },
                                    {
                                        "name": "hyvkw",
                                        "source": [
                                            {
                                                "context": "src",
                                                "element": "item",
                                                "variable": "item",
                                                "condition": "linkId = EmCare.A.DE05"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "ysjid",
                                                "source": [
                                                    {
                                                        "context": "item",
                                                        "element": "answer",
                                                        "listMode": "first",
                                                        "variable": "a"
                                                    }
                                                ],
                                                "rule": [
                                                    {
                                                        "name": "qutpp",
                                                        "source": [
                                                            {
                                                                "context": "a",
                                                                "element": "value",
                                                                "variable": "val"
                                                            }
                                                        ],
                                                        "target": [
                                                            {
                                                                "context": "tgt",
                                                                "contextType": "variable",
                                                                "element": "given",
                                                                "transform": "copy",
                                                                "parameter": [
                                                                    {
                                                                        "valueId": "val"
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    },
                                    {
                                        "name": "hmuyh",
                                        "source": [
                                            {
                                                "context": "src",
                                                "element": "item",
                                                "variable": "item",
                                                "condition": "linkId = EmCare.A.DE06"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "gbeml",
                                                "source": [
                                                    {
                                                        "context": "item",
                                                        "element": "answer",
                                                        "listMode": "first",
                                                        "variable": "a"
                                                    }
                                                ],
                                                "rule": [
                                                    {
                                                        "name": "nyuyd",
                                                        "source": [
                                                            {
                                                                "context": "a",
                                                                "element": "value",
                                                                "variable": "val"
                                                            }
                                                        ],
                                                        "target": [
                                                            {
                                                                "context": "tgt",
                                                                "contextType": "variable",
                                                                "element": "family",
                                                                "transform": "copy",
                                                                "parameter": [
                                                                    {
                                                                        "valueId": "val"
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "MapValueSetExtCodeemcareade16",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "ndkeo",
                                "source": [
                                    {
                                        "context": "src"
                                    }
                                ],
                                "target": [
                                    {
                                        "contextType": "variable",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueId": "tgt"
                                            }
                                        ]
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "mapbase",
                                        "source": [
                                            {
                                                "context": "src"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "contextType": "variable",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueId": "tgt"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "jrieo",
                                                "source": [
                                                    {
                                                        "context": "src",
                                                        "condition": "value.code = 'EmCare.A.DE17'"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "gender",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "female"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            },
                                            {
                                                "name": "ysukh",
                                                "source": [
                                                    {
                                                        "context": "src",
                                                        "condition": "value.code = 'EmCare.A.DE18'"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "gender",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "male"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            },
                                            {
                                                "name": "fbcgu",
                                                "source": [
                                                    {
                                                        "context": "src",
                                                        "condition": "value.code = 'EmCare.A.DE19'"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "gender",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "unknown"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "getIdemcarepatient",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "vsgnd",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "listMode": "first",
                                        "variable": "item",
                                        "condition": "linkId = 'emcarepatientid'"
                                    }
                                ],
                                "target": [
                                    {
                                        "contextType": "variable",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueId": "tgt"
                                            }
                                        ]
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "hunkz",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "contextType": "variable",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueId": "tgt"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "mhhco",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "id",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "getFullUrlemcarepatient",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "blgdd",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "listMode": "first",
                                        "variable": "item",
                                        "condition": "linkId = 'emcarepatientid'"
                                    }
                                ],
                                "target": [
                                    {
                                        "contextType": "variable",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueId": "tgt"
                                            }
                                        ]
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "cfzjo",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "contextType": "variable",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueId": "tgt"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "wjzds",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "fullUrl",
                                                        "transform": "append",
                                                        "parameter": [
                                                            {
                                                                "valueString": "urn:uuid:"
                                                            },
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "getUrlemcarepatient",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "wdosi",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "listMode": "first",
                                        "variable": "item",
                                        "condition": "linkId = 'emcarepatientid'"
                                    }
                                ],
                                "target": [
                                    {
                                        "contextType": "variable",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueId": "tgt"
                                            }
                                        ]
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "ljeyg",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "contextType": "variable",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueId": "tgt"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "xgxxa",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "ref",
                                                        "contextType": "variable",
                                                        "element": "reference",
                                                        "transform": "append",
                                                        "parameter": [
                                                            {
                                                                "valueString": "/Patient/"
                                                            },
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "emcarerelatedpersoncaregiver",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "type": "questionnaireResponse",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "type": "RelatedPerson",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "emcareade21",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "listMode": "first",
                                        "variable": "item",
                                        "condition": "(linkId = 'EmCare.A.DE21') or (linkId = 'EmCare.A.DE22') or (linkId = 'EmCare.A.DE23')"
                                    }
                                ],
                                "target": [
                                    {
                                        "contextType": "variable",
                                        "variable": "target",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueId": "tgt"
                                            }
                                        ]
                                    },
                                    {
                                        "context": "target",
                                        "contextType": "variable",
                                        "element": "name",
                                        "variable": "name"
                                    }
                                ],
                                "dependent": [
                                    {
                                        "name": "SetOfficalGivenNameemcarerelatedpersoncaregiver",
                                        "variable": [
                                            "src",
                                            "name"
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcareade35",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'EmCare.A.DE35'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "aemcareade35",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "aemcareade35",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "telecom",
                                                        "variable": "tel"
                                                    },
                                                    {
                                                        "context": "tel",
                                                        "contextType": "variable",
                                                        "element": "system",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "phone"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "tel",
                                                        "contextType": "variable",
                                                        "element": "use",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "mobile"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "tel",
                                                        "contextType": "variable",
                                                        "element": "value",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcareade36",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'EmCare.A.DE36'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "aemcareade36",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "aemcareade36",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "telecom",
                                                        "variable": "tel"
                                                    },
                                                    {
                                                        "context": "tel",
                                                        "contextType": "variable",
                                                        "element": "system",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "phone"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "tel",
                                                        "contextType": "variable",
                                                        "element": "use",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "home"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "tel",
                                                        "contextType": "variable",
                                                        "element": "value",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcareade37",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'EmCare.A.DE37'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "aemcareade37",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "aemcareade37",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "telecom",
                                                        "variable": "tel"
                                                    },
                                                    {
                                                        "context": "tel",
                                                        "contextType": "variable",
                                                        "element": "system",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "phone"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "tel",
                                                        "contextType": "variable",
                                                        "element": "use",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueString": "work"
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "context": "tel",
                                                        "contextType": "variable",
                                                        "element": "value",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "emcarerelatedpersoncaregiverid",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "variable": "item",
                                        "condition": "linkId = 'emcarerelatedpersoncaregiverid'"
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "aemcarerelatedpersoncaregiverid",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "contextType": "variable",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueId": "tgt"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "patient",
                                                "source": [
                                                    {
                                                        "context": "src",
                                                        "element": "subject",
                                                        "variable": "sub"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "patient",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "sub"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "SetOfficalGivenNameemcarerelatedpersoncaregiver",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "wlaes",
                                "source": [
                                    {
                                        "context": "src"
                                    }
                                ],
                                "target": [
                                    {
                                        "context": "tgt",
                                        "contextType": "variable",
                                        "element": "use",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueString": "official"
                                            }
                                        ]
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "lesds",
                                        "source": [
                                            {
                                                "context": "src",
                                                "element": "item",
                                                "variable": "item",
                                                "condition": "linkId = EmCare.A.DE21"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "cknsv",
                                                "source": [
                                                    {
                                                        "context": "item",
                                                        "element": "answer",
                                                        "listMode": "first",
                                                        "variable": "a"
                                                    }
                                                ],
                                                "rule": [
                                                    {
                                                        "name": "fjpoa",
                                                        "source": [
                                                            {
                                                                "context": "a",
                                                                "element": "value",
                                                                "variable": "val"
                                                            }
                                                        ],
                                                        "target": [
                                                            {
                                                                "context": "tgt",
                                                                "contextType": "variable",
                                                                "element": "given",
                                                                "transform": "copy",
                                                                "parameter": [
                                                                    {
                                                                        "valueId": "val"
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    },
                                    {
                                        "name": "xsusg",
                                        "source": [
                                            {
                                                "context": "src",
                                                "element": "item",
                                                "variable": "item",
                                                "condition": "linkId = EmCare.A.DE22"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "pklej",
                                                "source": [
                                                    {
                                                        "context": "item",
                                                        "element": "answer",
                                                        "listMode": "first",
                                                        "variable": "a"
                                                    }
                                                ],
                                                "rule": [
                                                    {
                                                        "name": "depys",
                                                        "source": [
                                                            {
                                                                "context": "a",
                                                                "element": "value",
                                                                "variable": "val"
                                                            }
                                                        ],
                                                        "target": [
                                                            {
                                                                "context": "tgt",
                                                                "contextType": "variable",
                                                                "element": "given",
                                                                "transform": "copy",
                                                                "parameter": [
                                                                    {
                                                                        "valueId": "val"
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    },
                                    {
                                        "name": "irrmx",
                                        "source": [
                                            {
                                                "context": "src",
                                                "element": "item",
                                                "variable": "item",
                                                "condition": "linkId = EmCare.A.DE23"
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "npzuj",
                                                "source": [
                                                    {
                                                        "context": "item",
                                                        "element": "answer",
                                                        "listMode": "first",
                                                        "variable": "a"
                                                    }
                                                ],
                                                "rule": [
                                                    {
                                                        "name": "vdbbh",
                                                        "source": [
                                                            {
                                                                "context": "a",
                                                                "element": "value",
                                                                "variable": "val"
                                                            }
                                                        ],
                                                        "target": [
                                                            {
                                                                "context": "tgt",
                                                                "contextType": "variable",
                                                                "element": "family",
                                                                "transform": "copy",
                                                                "parameter": [
                                                                    {
                                                                        "valueId": "val"
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "getIdemcarerelatedpersoncaregiver",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "qjgvm",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "listMode": "first",
                                        "variable": "item",
                                        "condition": "linkId = 'emcarerelatedpersoncaregiverid'"
                                    }
                                ],
                                "target": [
                                    {
                                        "contextType": "variable",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueId": "tgt"
                                            }
                                        ]
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "lnndz",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "contextType": "variable",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueId": "tgt"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "kagpz",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "id",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "getFullUrlemcarerelatedpersoncaregiver",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "fcofh",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "listMode": "first",
                                        "variable": "item",
                                        "condition": "linkId = 'emcarerelatedpersoncaregiverid'"
                                    }
                                ],
                                "target": [
                                    {
                                        "contextType": "variable",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueId": "tgt"
                                            }
                                        ]
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "yiygw",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "contextType": "variable",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueId": "tgt"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "mripo",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "tgt",
                                                        "contextType": "variable",
                                                        "element": "fullUrl",
                                                        "transform": "append",
                                                        "parameter": [
                                                            {
                                                                "valueString": "urn:uuid:"
                                                            },
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "getUrlemcarerelatedpersoncaregiver",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "ygqpk",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "item",
                                        "listMode": "first",
                                        "variable": "item",
                                        "condition": "linkId = 'emcarerelatedpersoncaregiverid'"
                                    }
                                ],
                                "target": [
                                    {
                                        "contextType": "variable",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueId": "tgt"
                                            }
                                        ]
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "xutdy",
                                        "source": [
                                            {
                                                "context": "item",
                                                "element": "answer",
                                                "listMode": "first",
                                                "variable": "a"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "contextType": "variable",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueId": "tgt"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "aozdo",
                                                "source": [
                                                    {
                                                        "context": "a",
                                                        "element": "value",
                                                        "variable": "val"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "context": "ref",
                                                        "contextType": "variable",
                                                        "element": "reference",
                                                        "transform": "append",
                                                        "parameter": [
                                                            {
                                                                "valueString": "/RelatedPerson/"
                                                            },
                                                            {
                                                                "valueId": "val"
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "emcarecommunicationrequestemcareade38",
                        "typeMode": "none",
                        "input": [
                            {
                                "name": "src",
                                "mode": "source"
                            },
                            {
                                "name": "tgt",
                                "mode": "target"
                            }
                        ],
                        "rule": [
                            {
                                "name": "jymjl",
                                "source": [
                                    {
                                        "context": "src"
                                    }
                                ],
                                "target": [
                                    {
                                        "context": "tgt",
                                        "contextType": "variable",
                                        "element": "category",
                                        "variable": "cc",
                                        "transform": "create",
                                        "parameter": [
                                            {
                                                "valueString": "CodeableConcept"
                                            }
                                        ]
                                    },
                                    {
                                        "context": "cc",
                                        "contextType": "variable",
                                        "element": "coding",
                                        "variable": "c",
                                        "transform": "create",
                                        "parameter": [
                                            {
                                                "valueString": "Coding"
                                            }
                                        ]
                                    },
                                    {
                                        "context": "c",
                                        "contextType": "variable",
                                        "element": "system",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueString": "http://hl7.org/fhir/ValueSet/communication-category"
                                            }
                                        ]
                                    },
                                    {
                                        "context": "c",
                                        "contextType": "variable",
                                        "element": "code",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueString": "notification"
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "quest",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "questionnaire",
                                        "variable": "q"
                                    }
                                ],
                                "target": [
                                    {
                                        "context": "tgt",
                                        "contextType": "variable",
                                        "element": "about",
                                        "variable": "ref",
                                        "transform": "create",
                                        "parameter": [
                                            {
                                                "valueString": "Reference"
                                            }
                                        ]
                                    },
                                    {
                                        "context": "ref",
                                        "contextType": "variable",
                                        "element": "type",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueString": "Questionnaire"
                                            }
                                        ]
                                    },
                                    {
                                        "context": "ref",
                                        "contextType": "variable",
                                        "element": "reference",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueId": "q"
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "fmctx",
                                "source": [
                                    {
                                        "context": "src",
                                        "element": "subject",
                                        "variable": "subject"
                                    }
                                ],
                                "target": [
                                    {
                                        "context": "tgt",
                                        "contextType": "variable",
                                        "element": "subject",
                                        "transform": "copy",
                                        "parameter": [
                                            {
                                                "valueId": "subject"
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "name": "epnjz",
                                "source": [
                                    {
                                        "context": "src"
                                    }
                                ],
                                "target": [
                                    {
                                        "context": "tgt",
                                        "contextType": "variable",
                                        "element": "recipient",
                                        "variable": "ref",
                                        "transform": "create",
                                        "parameter": [
                                            {
                                                "valueString": "Reference"
                                            }
                                        ]
                                    }
                                ],
                                "rule": [
                                    {
                                        "name": "ulytk",
                                        "source": [
                                            {
                                                "context": "src"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "context": "ref",
                                                "contextType": "variable",
                                                "element": "type",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueString": "RelatedPerson"
                                                    }
                                                ]
                                            }
                                        ]
                                    },
                                    {
                                        "name": "sdulh",
                                        "source": [
                                            {
                                                "context": "src",
                                                "element": "item",
                                                "listMode": "first",
                                                "variable": "item",
                                                "condition": "linkId = 'emcarerelatedpersoncaregiveruuid'"
                                            }
                                        ],
                                        "target": [
                                            {
                                                "contextType": "variable",
                                                "transform": "copy",
                                                "parameter": [
                                                    {
                                                        "valueId": "tgt"
                                                    }
                                                ]
                                            }
                                        ],
                                        "rule": [
                                            {
                                                "name": "bswyu",
                                                "source": [
                                                    {
                                                        "context": "item",
                                                        "element": "answer",
                                                        "listMode": "first",
                                                        "variable": "a"
                                                    }
                                                ],
                                                "target": [
                                                    {
                                                        "contextType": "variable",
                                                        "transform": "copy",
                                                        "parameter": [
                                                            {
                                                                "valueId": "tgt"
                                                            }
                                                        ]
                                                    }
                                                ],
                                                "rule": [
                                                    {
                                                        "name": "chfra",
                                                        "source": [
                                                            {
                                                                "context": "a",
                                                                "element": "value",
                                                                "variable": "val"
                                                            }
                                                        ],
                                                        "target": [
                                                            {
                                                                "context": "ref",
                                                                "contextType": "variable",
                                                                "element": "reference",
                                                                "transform": "append",
                                                                "parameter": [
                                                                    {
                                                                        "valueString": "/RelatedPerson/"
                                                                    },
                                                                    {
                                                                        "valueId": "val"
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }"""
        val structureMap = FhirContext.forR4().newJsonParser().parseResource(structureMapString) as StructureMap
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
                StructureMapExtractionContext(context = application.applicationContext) { _, worker -> structureMap
//                    StructureMapUtilities(worker).parse(structureMapString, "")
                }
            )
            val patientId = questionnaireResponse.subject.identifier.value
            val encounterId = questionnaireResponse.encounter.id
            saveResourcesFromBundle(extractedBundle, patientId, encounterId, facilityId)
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