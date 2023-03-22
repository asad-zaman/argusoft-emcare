package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.ui.common.model.*

interface Api {

    suspend fun login(requestMap: Map<String, String>): ApiResponse<User>

    suspend fun addDevice(deviceInfo: DeviceDetails): ApiResponse<DeviceDetails>

    suspend fun getDevice(deviceUUID: String): ApiResponse<DeviceDetails>

    suspend fun signup(signupRequest: SignupRequest) : ApiResponse<Any>

    suspend fun getRoles(): ApiResponse<List<Role>>

    suspend fun getFacilities(): ApiResponse<List<Facility>>

    suspend fun getLoggedInUser(): ApiResponse<LoggedInUser>

    suspend fun getLanguages(): ApiResponse<List<Language>>

    suspend fun getConsultationFlow(): ApiResponse<List<ConsultationFlowItem>>

    suspend fun getConsultationFlowWithTimestamp(timestamp: String): ApiResponse<List<ConsultationFlowItem>>

    suspend fun saveConsultations(consultations: List<ConsultationFlowItem>): ApiResponse<Any>
}
