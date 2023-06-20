package com.argusoft.who.emcare.ui.home.fhirResources;

import android.app.Application;

import com.argusoft.who.emcare.data.local.pref.Preference;
import com.google.android.fhir.FhirEngine;
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.search.search
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.AuditEvent
import org.hl7.fhir.r4.model.ResourceType

import javax.inject.Inject;

class FhirResourcesRepository @Inject constructor(
    private val fhirEngine:FhirEngine
) {
    suspend fun saveAudit(audit: AuditEvent) = flow {
        fhirEngine.create(audit)
        emit("saved")
    }

     suspend fun purgeAllAudits() = flow {
         CoroutineScope(Dispatchers.IO).launch {
             val allAudits = fhirEngine.search<AuditEvent> {}
            allAudits.forEach {
                try{
                    fhirEngine.purge(type = ResourceType.AuditEvent, id = it.id.removePrefix("AuditEvent/"), forcePurge = true)
                } catch (e: ResourceNotFoundException) {
                    print(e.localizedMessage)
                }
            }
        }
        emit("Done")
     }
}