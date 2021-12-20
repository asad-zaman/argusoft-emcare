package com.argusoft.who.emcare.ui.common.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignupRequest(
    @Json(name = "email")
    var email: String? = null,
    @Json(name = "firstName")
    var firstName: String? = null,
    @Json(name = "lastName")
    var lastName: String? = null,
    @Json(name = "locationId")
    var locationId: Int? = null,
    @Json(name = "password")
    var password: String? = null,
    @Json(name = "roleName")
    var roleName: String? = null,
    @Json(name = "regRequestFrom")
    var regRequestFrom: String? = "mobile"
)