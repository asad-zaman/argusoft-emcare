package com.argusoft.who.emcare.data.local.database

import com.argusoft.who.emcare.ui.common.model.Language
import com.argusoft.who.emcare.ui.common.model.Location
import com.argusoft.who.emcare.ui.common.model.LoggedInUser

interface Database {
    suspend fun saveLocations(locations: List<Location>)

    suspend fun getLocationById(id: Int): Location?

    suspend fun getChildLocations(id: Int?): List<Location>?

    suspend fun saveLanguages(languages: List<Language>)

    suspend fun getAllLanguages(): List<Language>?

    suspend fun saveLoginUser(loginUser: LoggedInUser)

    suspend fun loginUser(username: String, password: String): LoggedInUser?

    suspend fun getAllUser(): List<LoggedInUser>?

    suspend fun getLanguageByCode(languageCode: String): Language?

}