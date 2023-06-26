package com.argusoft.who.emcare.ui.home.fhirResources;

import android.app.Application;
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum

import com.argusoft.who.emcare.data.local.pref.Preference;
import com.google.android.fhir.FhirEngine;
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.search.search
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.AuditEvent
import org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentComponent
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.ResourceType

import javax.inject.Inject;

class FhirResourcesRepository @Inject constructor(
    private val fhirEngine:FhirEngine,
    private val preference: Preference
) {

    private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

    suspend fun saveAudit(audit: AuditEvent) = flow {
        fhirEngine.create(audit)
        emit("saved")
    }

     suspend fun purgeAllAudits() = flow {
         CoroutineScope(Dispatchers.IO).launch {
             val allAudits = fhirEngine.search<AuditEvent> {}
            allAudits.forEach {
                try{
                    fhirEngine.purge(type = ResourceType.AuditEvent, id = it.id.removePrefix("AuditEvent/"))
                } catch (e: Exception) {
                    print(e.localizedMessage)
                }
            }
        }
        emit("Done")
     }

    suspend fun purgeStartEndAuditsOnException() = flow {
        CoroutineScope(Dispatchers.IO).launch {
            val startAudit = parser.parseResource(preference.getStartAudit()) as AuditEvent
            val endAudit = parser.parseResource(preference.getEndAudit()) as AuditEvent
            fhirEngine.purge(type = ResourceType.AuditEvent, id = startAudit.id.removePrefix("AuditEvent/"), forcePurge = true)
            fhirEngine.purge(type = ResourceType.AuditEvent, id = endAudit.id.removePrefix("AuditEvent/"), forcePurge = true)
        }
        emit("Done")
    }
}