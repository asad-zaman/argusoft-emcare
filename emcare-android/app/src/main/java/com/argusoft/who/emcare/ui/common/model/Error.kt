package com.argusoft.who.emcare.ui.common.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Error(
    @Json(name = "error")
    var error: String? = null,
    @Json(name = "statusCode")
    var statusCode: Int? = null,
    @Json(name = "errorMessage")
    var errorMessage: String? = null,
    @Json(name = "error_description")
    var errorDescription: String? = null
)