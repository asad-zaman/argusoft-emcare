package com.argusoft.who.emcare.oldstruct.api

import com.argusoft.who.emcare.oldstruct.model.DeviceInfo
import com.argusoft.who.emcare.oldstruct.static.CompanionValues
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type

interface DeviceManagementService {

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

    companion object {

        val nullOnEmptyConverterFactory = object : Converter.Factory() {
            fun converterFactory() = this
            override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
                val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
                override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
            }
        }

        fun create(): DeviceManagementService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpRequestInterceptor())
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(CompanionValues.LOCAL_BASE_URL)
                .client(client)
                .addConverterFactory(nullOnEmptyConverterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DeviceManagementService::class.java)
        }
    }
}
