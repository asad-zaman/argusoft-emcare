package com.argusoft.who.emcare.ui.home.fhirResources;

import android.app.Application;

import com.argusoft.who.emcare.data.local.pref.Preference;
import com.google.android.fhir.FhirEngine;
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.AuditEvent

import javax.inject.Inject;

class FhirResourcesRepository @Inject constructor(
    private val fhirEngine:FhirEngine,
    private val application:Application,
    private val preference:Preference
) {
    suspend fun saveAudit(audit: AuditEvent) = flow {
        fhirEngine.create(audit)
        emit("saved")
    }
}