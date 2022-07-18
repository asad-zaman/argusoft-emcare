package com.argusoft.who.emcare.data.local.database

import com.argusoft.who.emcare.ui.common.model.Facility
import com.argusoft.who.emcare.ui.common.model.Language
import com.argusoft.who.emcare.ui.common.model.LoggedInUser

interface Database {
    suspend fun saveFacilities(facilities: List<Facility>)

    suspend fun saveLanguages(languages: List<Language>)

    suspend fun getAllLanguages(): List<Language>?

    suspend fun saveLoginUser(loginUser: LoggedInUser)

    suspend fun loginUser(username: String, password: String): LoggedInUser?

    suspend fun getAllUser(): List<LoggedInUser>?

    suspend fun getLanguageByCode(languageCode: String): Language?

}