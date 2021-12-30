package com.argusoft.who.emcare.ui.common.model


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class LoggedInUser(
    @Json(name = "email")
    var email: String? = null,
    @Embedded
    @Json(name = "location")
    var location: Location? = null,
    @Json(name = "roles")
    var roles: List<String>? = null,
    @PrimaryKey
    @Json(name = "userId")
    var userId: String = "",
    @Json(name = "userName")
    var userName: String? = null,
    @Json(name = "password")
    var password: String? = null,
)
