package com.argusoft.who.emcare.ui.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class Language(
    @Json(name = "createdBy")
    var createdBy: String? = null,
    @Json(name = "createdOn")
    var createdOn: String? = null,
    @PrimaryKey
    @Json(name = "id")
    var id: Int = 0,
    @Json(name = "languageCode")
    var languageCode: String? = null,
    @Json(name = "languageData")
    var languageData: String? = null,
    @Json(name = "languageName")
    var languageName: String? = null,
    @Json(name = "modifiedBy")
    var modifiedBy: String? = null,
    @Json(name = "modifiedOn")
    var modifiedOn: String? = null,
)