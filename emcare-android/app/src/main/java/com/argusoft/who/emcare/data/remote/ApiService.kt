package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.ui.common.model.DeviceDetails
import com.argusoft.who.emcare.ui.common.model.SignupRequest
import com.argusoft.who.emcare.ui.common.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("device/add")
    suspend fun addDevice(
        @Body deviceInfo: DeviceDetails
    ): Response<DeviceDetails>

    @GET("device")
    suspend fun getDeviceByMacAddress(
        @Query("deviceUUID") macAddress: String
    ): Response<DeviceDetails>


    @GET("signup")
    suspend fun signup(
        @Body signupRequest: SignupRequest
    ): Response<User>
}
