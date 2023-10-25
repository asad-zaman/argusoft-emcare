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
import com.argusoft.who.emcare.ui.home.ValueSetResolver
import com.argusoft.who.emcare.utils.ComplexWorkerContext
import com.argusoft.who.emcare.widget.CustomBooleanChoiceViewHolderFactory
import com.argusoft.who.emcare.widget.CustomDisplayViewHolderFactory
import com.google.android.fhir.*
import com.google.android.fhir.datacapture.DataCaptureConfig
import com.google.android.fhir.datacapture.ExternalAnswerValueSetResolver
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.QuestionnaireItemViewHolderFactoryMatchersProviderFactory
import com.google.android.fhir.datacapture.XFhirQueryResolver
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.search
import com.google.android.fhir.sync.remote.HttpLogger
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ValueSet
import org.hl7.fhir.utilities.npm.NpmPackage
import timber.log.Timber
import java.io.File
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

    var contextR4 : ComplexWorkerContext? = null

    //Work Manager for EmcareSync Functionality
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    /*
        DataCaptureConfiguration for the SDC
        valueSetResolver for the answerOptions in questionnaire with valueSet URLs.
        xFhirQueryResolver to support X-Fhir-Query for the library.
        urlResolver for the Binary Resources.
     */
    private val dataCaptureConfiguration by lazy {
        DataCaptureConfig(
//            valueSetResolverExternal = object : ValueSetResolver(){},
            valueSetResolverExternal = object: ValueSetResolver() {
                override suspend fun resolve(uri: String): List<Coding> {
                    return fetchValueSetFromDb(uri)
                }
             },
            xFhirQueryResolver = { FhirEngineProvider.getInstance(this).search(it).map { it.resource} },
            urlResolver = ReferenceUrlResolver(this@EmCareApplication as Context),
            questionnaireItemViewHolderFactoryMatchersProviderFactory = QuestionnaireItemViewHolderFactoryMatchersProviderFactoryImpl
        )
    }

    private fun constructR4Context() = CoroutineScope(Dispatchers.IO).launch {
        println("**** creating contextR4")

        val measlesIg = async {
            NpmPackage.fromPackage(
                assets.open("package.r4.tgz")
            )
        }

        val baseIg = async {
            NpmPackage.fromPackage(
                assets.open("package.tgz")
            )
        }

        val packages = arrayListOf<NpmPackage>(
            measlesIg.await(),
            baseIg.await()
        )
        println("**** read assets contextR4")

        contextR4 = ComplexWorkerContext()
        contextR4?.apply {
            loadFromMultiplePackages(packages, true)
            println("**** created contextR4")
            ValueSetResolver.init(this@EmCareApplication, this)
        }
    }



    override fun getDataCaptureConfig(): DataCaptureConfig {
        return dataCaptureConfiguration
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        constructR4Context()

        //Initializing Fhir Engine
        FhirEngineProvider.init(
            FhirEngineConfiguration(
                enableEncryptionIfSupported = false,
                databaseErrorStrategy = DatabaseErrorStrategy.RECREATE_AT_OPEN,
                serverConfiguration = ServerConfiguration(
                    baseUrl = BuildConfig.FHIR_BASE_URL,
                    networkConfiguration = NetworkConfiguration(connectionTimeOut = 1200, readTimeOut = 1200, writeTimeOut = 1200, uploadWithGzip = true),
                    authenticator = EmcareAuthenticator(preference),
                    httpLogger = HttpLogger(
                    HttpLogger.Configuration(
                        if (BuildConfig.DEBUG) HttpLogger.Level.BODY else HttpLogger.Level.BASIC
                    )
                    ) { Timber.tag("App-HttpLog").d(it) }
                )
            )
        )
    }

    /*
        Returns List of Coding from ValueSets stored in the fhirEngine
     */
    private suspend fun lookupCodesFromDb(uri: String): List<Coding> {
        val valueSets: List<SearchResult<ValueSet>> = FhirEngineProvider.getInstance(this)
            .search {
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
            valueSet.resource.compose.include.forEach { includeObj ->
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

    private object QuestionnaireItemViewHolderFactoryMatchersProviderFactoryImpl :
        QuestionnaireItemViewHolderFactoryMatchersProviderFactory {
        override fun get(provider: String): QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider {
            return when (provider) {
                "CUSTOM" -> QuestionnaireItemViewHolderFactoryMatchersProviderImpl
                else -> EmptyQuestionnaireItemViewHolderFactoryMatchersProviderImpl
            }
        }
    }

    private object EmptyQuestionnaireItemViewHolderFactoryMatchersProviderImpl :
        QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider() {
        override fun get() = emptyList<QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher>()
    }

    private object QuestionnaireItemViewHolderFactoryMatchersProviderImpl :
        QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider() {
        override fun get() = listOf(
            QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher(
                CustomBooleanChoiceViewHolderFactory
            ) { questionnaireItem ->
                questionnaireItem.getExtensionByUrl(CustomBooleanChoiceViewHolderFactory.WIDGET_EXTENSION).let {
                    if(it == null) false else it?.value?.toString()?.contains(CustomBooleanChoiceViewHolderFactory.WIDGET_TYPE) == true
                }
            },
            QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher(
                CustomDisplayViewHolderFactory
            ) { questionnaireItem ->
                questionnaireItem.getExtensionByUrl(CustomDisplayViewHolderFactory.WIDGET_EXTENSION).let {
                    it!= null
                }
            }
        )
    }

    companion object {
        lateinit var instance: Application
            private set
        fun contextR4(context: Context) = (context.applicationContext as EmCareApplication).contextR4
    }
}