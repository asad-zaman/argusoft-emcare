package com.argusoft.who.emcare.data.local.database

import com.argusoft.who.emcare.ui.common.model.Facility
import com.argusoft.who.emcare.ui.common.model.Language
import com.argusoft.who.emcare.ui.common.model.LoggedInUser

class DatabaseManager(roomDatabase: RoomDatabase) : Database {

    private val dao = roomDatabase.dao()

    override suspend fun saveFacilities(facilities: List<Facility>) {
        dao.saveFacilities(facilities)
    }

    override suspend fun saveLanguages(languages: List<Language>) {
        dao.saveLanguages(languages)
    }

    override suspend fun getAllLanguages(): List<Language>? {
        return dao.getAllLanguages()
    }

    override suspend fun saveLoginUser(loginUser: LoggedInUser) {
        dao.saveLoginUser(loginUser)
    }

    override suspend fun loginUser(username: String, password: String): LoggedInUser? {
        return dao.loginUser(username, password)
    }

    override suspend fun getAllUser(): List<LoggedInUser>? {
        return dao.getAllUser()
    }

    override suspend fun getLanguageByCode(languageCode: String): Language? {
        return dao.getLanguageByCode(languageCode)
    }
}