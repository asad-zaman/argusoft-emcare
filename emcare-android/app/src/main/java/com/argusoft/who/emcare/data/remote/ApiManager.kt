package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.ui.common.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
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
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                if (preference.getToken().isNotEmpty())
                    request.addHeader(
                        "Authorization",
                        "Bearer ${preference.getToken()}"
                    )
                return@addInterceptor chain.proceed(request.build())
            }
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        return@lazy okHttpClientBuilder
    }

    private val keyCloakApiService: KeyCloakApiService by lazy {
        return@lazy Retrofit.Builder()
            .baseUrl(BuildConfig.KEYCLOAK_BASE_URL)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(KeyCloakApiService::class.java)
    }

    private val apiService: ApiService by lazy {
        return@lazy Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override suspend fun login(requestMap: Map<String, String>): ApiResponse<User> {
        return executeApiHelper {
            keyCloakApiService.getAccessToken(requestMap)
        }
    }

    override suspend fun addDevice(deviceInfo: DeviceDetails): ApiResponse<DeviceDetails> {
        return executeApiHelper { apiService.addDevice(deviceInfo) }
    }

    override suspend fun getDevice(deviceUUID: String): ApiResponse<DeviceDetails> {
        return executeApiHelper { apiService.getDeviceByMacAddress(deviceUUID) }
    }

    override suspend fun signup(signupRequest: SignupRequest): ApiResponse<Any> {
        return executeApiHelper { apiService.signup(signupRequest) }
    }


    override suspend fun getRoles(): ApiResponse<List<Role>> {
        return executeApiHelper { apiService.getRoles() }
    }

    override suspend fun getFacilities(): ApiResponse<List<Facility>> {
        return executeApiHelper { apiService.getFacilities() }
    }

    override suspend fun getLanguages(): ApiResponse<List<Language>> {
        return executeApiHelper { apiService.getLanguages() }
    }

    override suspend fun getConsultationFlow(): ApiResponse<List<ConsultationFlowItem>> {
        return executeApiHelper { apiService.getConsultationFlow() }
    }

    override suspend fun getLoggedInUser(): ApiResponse<LoggedInUser> {
        return executeApiHelper { apiService.getLoggedInUser() }
    }

    override suspend fun saveConsultations(consultations: List<ConsultationFlowItem>): ApiResponse<Any> {
        return executeApiHelper { apiService.saveConsultations(consultations) }
    }
}



