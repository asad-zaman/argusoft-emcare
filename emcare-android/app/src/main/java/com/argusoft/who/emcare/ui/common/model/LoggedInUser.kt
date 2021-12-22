package com.argusoft.who.emcare.ui.common.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoggedInUser(
    @Json(name = "email")
    var email: String? = null,
    @Json(name = "location")
    var location: Location? = null,
    @Json(name = "roles")
    var roles: List<String?>? = null,
    @Json(name = "userId")
    var userId: String? = null,
    @Json(name = "userName")
    var userName: String? = null
)
