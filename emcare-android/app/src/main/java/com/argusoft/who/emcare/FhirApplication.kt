package com.argusoft.who.emcare

import android.app.Application
import android.content.Context
import com.argusoft.who.emcare.data.FhirPeriodicSyncWorker
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.sync.Sync

class FhirApplication : Application() {
  // Only initiate the FhirEngine when used for the first time, not when the app is created.
  private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

  override fun onCreate() {
    super.onCreate()
  }


  private fun constructFhirEngine(): FhirEngine {
    return FhirEngineProvider.getInstance(this)
  }

  companion object {
    fun fhirEngine(context: Context) = (context.applicationContext as FhirApplication).fhirEngine
  }
}
