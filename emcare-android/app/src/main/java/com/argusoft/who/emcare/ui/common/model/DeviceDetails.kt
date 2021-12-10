package com.argusoft.who.emcare.ui.common.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceDetails(
    @Json(name = "androidVersion")
    var androidVersion: String? = null,
    @Json(name = "deviceModel")
    var deviceModel: String? = null,
    @Json(name = "deviceName")
    var deviceName: String? = null,
    @Json(name = "deviceOs")
    var deviceOs: String? = null,
    @Json(name = "deviceUUID")
    var deviceUUID: String? = null,
    @Json(name = "imeiNumber")
    var imeiNumber: String? = null,
    @Json(name = "isBlocked")
    var isBlocked: Boolean? = null,
    @Json(name = "lastLoggedInUser")
    var lastLoggedInUser: String? = null,
    @Json(name = "macAddress")
    var macAddress: String? = null
)