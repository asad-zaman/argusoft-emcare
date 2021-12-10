package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.oldstruct.model.DeviceInfo
import retrofit2.Call
import retrofit2.http.*

interface ApiService {


    @POST("/api/device/add")
    fun addDevice(
        @Body deviceInfo: DeviceInfo
    ): Call<DeviceInfo>

    @PUT("/api/device/update")
    fun updateDevice(
        @Body deviceInfo: DeviceInfo
    ): Call<DeviceInfo>

    @GET("/api/device")
    fun getDeviceByMacAddress(
        @Query("macAddress") macAddress: String
    ): Call<DeviceInfo>
}
