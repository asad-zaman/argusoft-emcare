package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.oldstruct.model.DeviceInfo
import com.argusoft.who.emcare.ui.common.model.DeviceDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("/api/device/add")
    suspend fun addDevice(
        @Body deviceInfo: DeviceDetails
    ): Response<DeviceDetails>

    @GET("/api/device")
    suspend fun getDeviceByMacAddress(
        @Query("deviceUUID") macAddress: String
    ): Response<DeviceDetails>
}
