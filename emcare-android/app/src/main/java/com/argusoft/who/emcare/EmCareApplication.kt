package com.argusoft.who.emcare

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.utils.localization.LocaleHelperApplicationDelegate
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class EmCareApplication : Application() {

    private val localeAppDelegate = LocaleHelperApplicationDelegate()

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

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localeAppDelegate.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeAppDelegate.onConfigurationChanged(this)
    }
}