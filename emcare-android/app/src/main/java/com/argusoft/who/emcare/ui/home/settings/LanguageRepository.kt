package com.argusoft.who.emcare.ui.home.settings

import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.remote.ApiResponse
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LanguageRepository @Inject constructor(
    private val database: Database
) {

    fun getAllLanguages() = flow {
        val list = database.getAllLanguages()
        emit(ApiResponse.Success(data = list))
    }

    fun getLanguageByCode(languageCode: String) = flow {
        val language = database.getLanguageByCode(languageCode)
        emit(ApiResponse.Success(data = language))
    }

}