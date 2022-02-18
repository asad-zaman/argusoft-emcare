package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.ui.common.model.*

interface Api {

    suspend fun login(requestMap: Map<String, String>): ApiResponse<User>

    suspend fun addDevice(deviceInfo: DeviceDetails): ApiResponse<DeviceDetails>

    suspend fun getDevice(deviceUUID: String): ApiResponse<DeviceDetails>

    suspend fun signup(signupRequest: SignupRequest) : ApiResponse<Any>

    fun getHapiFhirResourceDataSource() : HapiFhirResourceDataSource

    suspend fun getRoles(): ApiResponse<List<Role>>

    suspend fun getLocations(): ApiResponse<List<Location>>

    suspend fun getLoggedInUser(): ApiResponse<LoggedInUser>

    suspend fun getLanguages(): ApiResponse<List<Language>>
}
