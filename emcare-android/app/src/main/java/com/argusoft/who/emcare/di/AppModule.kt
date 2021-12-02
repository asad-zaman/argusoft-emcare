package com.argusoft.who.emcare.di

import android.content.Context
import androidx.room.Room
import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.database.DatabaseManager
import com.argusoft.who.emcare.data.local.database.RoomDatabase
import com.argusoft.who.emcare.data.local.pref.EncPref
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.local.pref.PreferenceManager
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.data.remote.ApiManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
            .setDebuggable(BuildConfig.DEBUG)
            .build()
    }

    @Singleton
    @Provides
    fun provideAppRoomDatabase(@ApplicationContext context: Context): RoomDatabase {
        return Room.databaseBuilder(
            context,
            RoomDatabase::class.java,
            "app.db"
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
    fun provideAppApi(): Api {
        return ApiManager()
    }
}