package com.argusoft.who.emcare.ui.common.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant

@Entity
@JsonClass(generateAdapter = true)
data class LoggedInUser(
    @Json(name = "email")
    var email: String? = null,
    @Json(name = "feature")
    var feature: List<Feature?>? = null,
    @Json(name = "firstName")
    var firstName: String? = null,
    @Json(name = "language")
    var language: String? = null,
    @Json(name = "lastName")
    var lastName: String? = null,
    @Json(name = "facilities")
    var facility: List<Facility>? = null,
    @Json(name = "roles")
    var roles: List<String?>? = null,
    @PrimaryKey
    @Json(name = "userId")
    var userId: String = "",
    @Json(name = "userName")
    var userName: String? = null,
    @Json(name = "password")
    var password: String? = null,
    var loginTime: Long = Instant.now().toEpochMilli()
){
    @JsonClass(generateAdapter = true)
    data class Feature(
        @Json(name = "featureJson")
        var featureJson: String? = null,
        @Json(name = "id")
        var id: Int? = null,
        @Json(name = "menuName")
        var menuName: String? = null
    )
}