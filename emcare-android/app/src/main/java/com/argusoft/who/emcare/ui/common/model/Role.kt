package com.argusoft.who.emcare.ui.common.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Role(
    @Json(name = "attributes")
    var attributes: Any? = null,
    @Json(name = "clientRole")
    var clientRole: Boolean? = null,
    @Json(name = "composite")
    var composite: Boolean? = null,
    @Json(name = "composites")
    var composites: Any? = null,
    @Json(name = "containerId")
    var containerId: String? = null,
    @Json(name = "description")
    var description: String? = null,
    @Json(name = "id")
    var id: String? = null,
    @Json(name = "name")
    var name: String? = null,
    @Json(name = "scopeParamRequired")
    var scopeParamRequired: Any? = null
)
