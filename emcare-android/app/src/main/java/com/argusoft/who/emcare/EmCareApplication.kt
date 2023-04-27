package com.argusoft.who.emcare

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.argusoft.who.emcare.data.local.ReferenceUrlResolver
import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.sync.EmcareAuthenticator
import com.google.android.fhir.*
import com.google.android.fhir.datacapture.DataCaptureConfig
import com.google.android.fhir.datacapture.ExternalAnswerValueSetResolver
import com.google.android.fhir.datacapture.XFhirQueryResolver
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.search
import com.google.android.fhir.sync.remote.HttpLogger
import dagger.hilt.android.HiltAndroidApp
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ValueSet
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class EmCareApplication : Application(), Configuration.Provider, DataCaptureConfig.Provider {

    @Inject
    lateinit var preference: Preference

    @Inject
    lateinit var database: Database

    @Inject
    lateinit var api: Api

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private val dataCaptureConfiguration by lazy {
        DataCaptureConfig(
            valueSetResolverExternal =
            object : ExternalAnswerValueSetResolver {
                override suspend fun resolve(uri: String): List<Coding> {
                    return lookupCodesFromDb(uri)
                }
            },
            xFhirQueryResolver = { FhirEngineProvider.getInstance(this).search(it) },
            urlResolver = ReferenceUrlResolver(this@EmCareApplication as Context)
        )
    }

    private suspend fun lookupCodesFromDb(uri: String): List<Coding> {
        val valueSets: List<ValueSet> = FhirEngineProvider.getInstance(this).search {
            filter(
                ValueSet.URL,
                {
                    StringFilterModifier.MATCHES_EXACTLY
                    value = uri
                }
            )
        }

        if (valueSets.isEmpty()) {
            return listOf()
        } else {
            val valueSet = valueSets.get(0)
            val codingList = mutableListOf<Coding>()
            valueSet.compose.include.forEach { includeObj ->
                run {
                    includeObj.concept.forEach { conceptObj ->
                        codingList.add(
                            Coding(
                                includeObj.system,
                                conceptObj.code,
                                conceptObj.display
                            )
                        )
                    }

                }
            }
            return codingList
        }
    }

    override fun getDataCaptureConfig(): DataCaptureConfig {
        return dataCaptureConfiguration
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        FhirEngineProvider.init(
            FhirEngineConfiguration(
                enableEncryptionIfSupported = false,
                DatabaseErrorStrategy.RECREATE_AT_OPEN,
                ServerConfiguration(BuildConfig.FHIR_BASE_URL,
                    NetworkConfiguration(connectionTimeOut = 600, readTimeOut = 600, writeTimeOut = 600),
                    EmcareAuthenticator(preference),
                    httpLogger =
                    HttpLogger(
                        HttpLogger.Configuration(
                            if (BuildConfig.DEBUG) HttpLogger.Level.BODY else HttpLogger.Level.BASIC
                        )
                    ) { Timber.tag("App-HttpLog").d(it) })
            )
        )
    }
}