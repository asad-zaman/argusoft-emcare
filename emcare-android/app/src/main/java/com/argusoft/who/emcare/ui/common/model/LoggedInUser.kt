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
) {
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
        @Json(name = "type")
        var type: String? = null
    )
}