package com.argusoft.who.emcare.ui.common.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceDetails(
    @Json(name = "createdBy")
    var createdBy: String? = null,

    @Json(name = "createdOn")
    var createdOn: String? = null,

    @Json(name = "modifiedBy")
    var modifiedBy: String? = null,

    @Json(name = "modifiedOn")
    var modifiedOn: String? = null,

    @Json(name = "deviceId")
    var deviceId: Int? = null,

    @Json(name = "androidVersion")
    var androidVersion: String? = null,

    @Json(name = "imeiNumber")
    var imeiNumber: String? = null,

    @Json(name = "macAddress")
    var macAddress: String? = null,

    @Json(name = "lastLoggedInUser")
    var lastLoggedInUser: String? = null,

    @Json(name = "isBlocked")
    var isBlocked: Boolean? = null,
)
