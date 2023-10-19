package com.argusoft.who.emcare.di

import android.content.Context
import androidx.room.Room
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.database.DatabaseManager
import com.argusoft.who.emcare.data.local.database.RoomDatabase
import com.argusoft.who.emcare.data.local.pref.EncPref
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.local.pref.PreferenceManager
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.data.remote.ApiManager
import com.argusoft.who.emcare.utils.common.NetworkHelper
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.workflow.FhirOperator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideAppEncSharedPref(@ApplicationContext context: Context): EncPref {
        return EncPref.Builder()
            .serPrefName(context.packageName)
            .setContext(context)
            .setDebuggable(false)
            .build()
    }

    @Singleton
    @Provides
    fun provideAppRoomDatabase(@ApplicationContext context: Context): RoomDatabase {
        return Room.databaseBuilder(
            context,
            RoomDatabase::class.java,
            "${BuildConfig.APPLICATION_ID}.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideAppPreference(encPref: EncPref): Preference {
        return PreferenceManager(encPref)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(roomDatabase: RoomDatabase): Database {
        return DatabaseManager(roomDatabase)
    }

    @Singleton
    @Provides
    fun provideAppApi(preference : Preference): Api {
        return ApiManager(preference)
    }

    @Singleton
    @Provides
    fun provideAppFhirEngine(@ApplicationContext context: Context): FhirEngine {
        return FhirEngineProvider.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideAppFhirOperator(@ApplicationContext context: Context, fhirEngine: FhirEngine, knowledgeManager: KnowledgeManager): FhirOperator {
        return FhirOperator.Builder(context)
            .fhirContext(FhirContext.forCached(FhirVersionEnum.R4))
            .fhirEngine(fhirEngine)
            .knowledgeManager(knowledgeManager)
            .build()
    }

    @Singleton
    @Provides
    fun provideAppIgManager(@ApplicationContext context: Context): KnowledgeManager {
        return KnowledgeManager.create(context)
    }

    @Singleton
    @Provides
    fun provideNetworkHelper(@ApplicationContext context: Context): NetworkHelper {
        return NetworkHelper(context)
    }

    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class IoDispatcher

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MainDispatcher

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DefaultDispatcher
}