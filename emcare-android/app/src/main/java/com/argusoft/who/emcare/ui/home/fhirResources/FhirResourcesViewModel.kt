package com.argusoft.who.emcare.ui.home.fhirResources;

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.DRAFT_AUDIT
import com.argusoft.who.emcare.ui.common.END_AUDIT
import com.argusoft.who.emcare.ui.common.START_AUDIT
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent


import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.AuditEvent
import org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentComponent
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.ResourceType
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

@HiltViewModel
class FhirResourcesViewModel @Inject constructor(
    private val fhirResourcesRepository: FhirResourcesRepository,
    private val preference: Preference
) : ViewModel() {

    private val _auditSaved = SingleLiveEvent<ApiResponse<String>>()
    val auditSaved: LiveData<ApiResponse<String>> = _auditSaved

    private val _draftAuditSaved = SingleLiveEvent<ApiResponse<String>>()
    val draftAuditSaved: LiveData<ApiResponse<String>> = _draftAuditSaved

    private val _resourcesPurged = SingleLiveEvent<ApiResponse<String>>()
    val resourcesPurged: LiveData<ApiResponse<String>> = _resourcesPurged

    private val parser = FhirContext.forR4().newJsonParser()

    fun createStartAudit(consultationStage: String, patientId: String, encounterId: String) {
        val startAudit = createAudit(
            consultationStage = consultationStage,
            category = START_AUDIT,
            patientId = patientId,
            encounterId = encounterId
        )
        preference.setStartAudit(
            parser.encodeResourceToString(startAudit)
        )
    }

    fun saveStartAndEndAudit(consultationStage: String, patientId: String, encounterId: String) {
        val endAudit = createAudit(
            consultationStage = consultationStage,
            category = END_AUDIT,
            patientId = patientId,
            encounterId = encounterId
        )
        preference.setEndAudit(parser.encodeResourceToString(endAudit))

        val startAuditString = preference.getStartAudit()
        val startAuditObject = parser.parseResource(startAuditString) as AuditEvent
        viewModelScope.launch {
            fhirResourcesRepository.saveAudit(startAuditObject).collect{
                fhirResourcesRepository.saveAudit(endAudit).collect{
                    _auditSaved.value = ApiResponse.Success("Done")
                }
            }
        }
    }

    fun saveStartAndDraftAudit(consultationStage: String, patientId: String, encounterId: String) {
        val draftAudit = createAudit(
            consultationStage = consultationStage,
            category = DRAFT_AUDIT,
            patientId = patientId,
            encounterId = encounterId
        )
        val startAuditString = preference.getStartAudit()
        val startAuditObject = parser.parseResource(startAuditString) as AuditEvent
        viewModelScope.launch {
            fhirResourcesRepository.saveAudit(startAuditObject).collect{
                fhirResourcesRepository.saveAudit(draftAudit).collect{
                    _draftAuditSaved.value = ApiResponse.Success("Done")
                }
            }
        }
    }

    fun purgeAllAudits() {
        viewModelScope.launch {
            fhirResourcesRepository.purgeAllAudits().collect {
                _resourcesPurged.value = ApiResponse.Success("Done")
            }
        }
    }
    private fun createAudit(consultationStage: String, category: String, patientId: String, encounterId: String) : AuditEvent{
        val recordedElementString = ZonedDateTime.now(ZoneId.of("UTC")).toString().removeSuffix("Z[UTC]").plus("+00:00")
        return AuditEvent().apply {
            id = UUID.randomUUID().toString()
            type = Coding().apply {
                display = category
                code = category
            }
            recordedElement = InstantType(recordedElementString)
            agent = mutableListOf(
                AuditEventAgentComponent(BooleanType(true)).apply {
                    type = CodeableConcept().apply {
                        text = "Patient"
                    }
                    who = Reference().apply {
                        type = ResourceType.Patient.name
                        identifier = Identifier().apply {
                            value = patientId
                        }
                    }
                },
                AuditEventAgentComponent(BooleanType(true)).apply {
                    type = CodeableConcept().apply {
                        text = "Encounter"
                    }
                    who = Reference().apply {
                        type = ResourceType.Encounter.name
                        identifier = Identifier().apply {
                            value = encounterId
                        }
                    }
                },
                AuditEventAgentComponent(BooleanType(true)).apply {
                    type = CodeableConcept().apply {
                        text = "Consultation Stage"
                    }
                    who = Reference().apply {
                        identifier = Identifier().apply {
                            value = consultationStage
                        }
                    }
                }
            )

        }
    }
}