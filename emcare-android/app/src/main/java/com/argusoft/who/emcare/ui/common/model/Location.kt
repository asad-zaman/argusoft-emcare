package com.argusoft.who.emcare.ui.common.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Location(
    @Json(name = "active")
    var active: Boolean? = null,
    @Json(name = "createdBy")
    var createdBy: String? = null,
    @Json(name = "createdOn")
    var createdOn: String? = null,
    @Json(name = "id")
    var id: Int? = null,
    @Json(name = "modifiedBy")
    var modifiedBy: String? = null,
    @Json(name = "modifiedOn")
    var modifiedOn: String? = null,
    @Json(name = "name")
    var name: String? = null,
    @Json(name = "parent")
    var parent: Int? = null,
    @Json(name = "parentName")
    var parentName: String? = null,
    @Json(name = "type")
    var type: String? = null,
)
