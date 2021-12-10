package com.argusoft.who.emcare.ui.common.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "access_token")
    var accessToken: String? = null,
    @Json(name = "expires_in")
    var expiresIn: Int? = null,
    @Json(name = "id_token")
    var idToken: String? = null,
    @Json(name = "not-before-policy")
    var notBeforePolicy: Int? = null,
    @Json(name = "refresh_expires_in")
    var refreshExpiresIn: Int? = null,
    @Json(name = "refresh_token")
    var refreshToken: String? = null,
    @Json(name = "scope")
    var scope: String? = null,
    @Json(name = "session_state")
    var sessionState: String? = null,
    @Json(name = "token_type")
    var tokenType: String? = null,
    @Json(name = "error")
    var error: String? = null,
    @Json(name = "error_description")
    var errorDescription: String? = null
)