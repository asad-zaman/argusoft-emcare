package com.argusoft.who.emcare.data

import android.content.Context
import androidx.work.WorkerParameters
import com.argusoft.who.emcare.api.ServerFhirService
import ca.uhn.fhir.context.FhirContext
import com.argusoft.who.emcare.FhirApplication
import com.google.android.fhir.sync.FhirSyncWorker
import org.hl7.fhir.r4.model.ResourceType

class FhirPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  FhirSyncWorker(appContext, workerParams) {

  override fun getSyncData() = mapOf(ResourceType.Patient to mapOf("address-city" to "GANDHINAGAR"))

  override fun getDataSource() =
    HapiFhirResourceDataSource(ServerFhirService.create(FhirContext.forR4().newJsonParser()))

  override fun getFhirEngine() = FhirApplication.fhirEngine(applicationContext)
}
