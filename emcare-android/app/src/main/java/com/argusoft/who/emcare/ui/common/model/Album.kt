package com.argusoft.who.emcare.ui.common.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
@Entity
data class Album(
    @Json(name = "albumId")
    val albumId: Int? = null,

    @Json(name = "id")
    @PrimaryKey
    val id: Int? = null,

    @Json(name = "title")
    val title: String? = null,

    @Json(name = "url")
    val url: String? = null,

    @Json(name = "thumbnailUrl")
    val thumbnailUrl: String? = null
) : Parcelable
