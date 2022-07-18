package com.argusoft.who.emcare.ui.common.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class Facility(
    @Json(name = "address")
    var address: String? = null,
    @PrimaryKey
    @Json(name = "facilityId")
    var facilityId: String = "",
    @Json(name = "facilityName")
    var facilityName: String? = null,
    @Json(name = "locationId")
    var locationId: Int? = null,
    @Json(name = "locationName")
    var locationName: String? = null,
    @Json(name = "organizationId")
    var organizationId: String? = null,
    @Json(name = "organizationName")
    var organizationName: String? = null,
    @Json(name = "status")
    var status: String? = null
)