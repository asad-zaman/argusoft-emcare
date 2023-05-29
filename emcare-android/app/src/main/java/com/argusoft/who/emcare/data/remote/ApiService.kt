package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.ui.common.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body filedMap: Map<String, String>
    ): Response<User>

    @POST("device/add")
    suspend fun addDevice(
        @Body deviceInfo: DeviceDetails
    ): Response<DeviceDetails>

    @GET("device")
    suspend fun getDeviceByMacAddress(
        @Query("deviceUUID") macAddress: String
    ): Response<DeviceDetails>

    @POST("signup")
    suspend fun signup(
        @Body signupRequest: SignupRequest
    ): Response<Any>

    @GET("signup/roles")
    suspend fun getRoles(): Response<List<Role>>

    @GET("open/active/facility")
    suspend fun getFacilities(): Response<List<Facility>>

    @GET("open/country/list")
    suspend fun getCountries(): Response<List<String>>

    @GET("language/all")
    suspend fun getLanguages(): Response<List<Language>>

    @GET("user")
    suspend fun getLoggedInUser(): Response<LoggedInUser>

    @GET("questionnaire_response/fetch/all")
    suspend fun getConsultationFlow(): Response<List<ConsultationFlowItem>>

    @GET("questionnaire_response/fetch/all")
    suspend fun getConsultationFlowWithTimestamp(@Query("_lastUpdated") timestamp: String): Response<List<ConsultationFlowItem>>

    @POST("questionnaire_response/createOrUpdate")
    suspend fun saveConsultations(
        @Body consultations: List<ConsultationFlowItem>
    ): Response<Any>
}
