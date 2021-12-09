package com.argusoft.who.emcare

import android.app.Application
import android.content.Context
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class EmCareApplication : Application() {

    companion object {
        fun fhirEngine(context: Context) = (context.applicationContext as EmCareApplication).fhirEngine
    }

    private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

    private fun constructFhirEngine(): FhirEngine {
        return FhirEngineProvider.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}