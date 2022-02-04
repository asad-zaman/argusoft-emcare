package com.argusoft.who.emcare.ui.home.location

import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.data.remote.UNEXPECTED_INTERNAL_SERVER
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val database: Database,
    private val preference: Preference
) {

    fun getLocations() = flow {
        preference.getLoggedInUser()?.location?.let {
            val list = database.getChildLocations(it.id)
            emit(ApiResponse.Success(data = list))
        } ?: emit(ApiResponse.ApiError(UNEXPECTED_INTERNAL_SERVER))
    }
}