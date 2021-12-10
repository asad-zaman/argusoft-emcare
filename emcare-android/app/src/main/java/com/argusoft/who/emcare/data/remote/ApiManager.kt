package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.ui.common.model.User
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class ApiManager(private val preference: Preference) : Api {

    private val okHttpClientBuilder: OkHttpClient.Builder by lazy {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        return@lazy okHttpClientBuilder
    }

    private val keyCloakKeyCloakApiService: KeyCloakApiService by lazy {
        return@lazy Retrofit.Builder()
            .baseUrl(BuildConfig.KEYCLOAK_BASE_URL)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(KeyCloakApiService::class.java)
    }

    override suspend fun login(requestMap: Map<String, String>): ApiResponse<User> {
        return executeApiHelper {
            keyCloakKeyCloakApiService.getAccessToken(requestMap)
        }
    }
}



