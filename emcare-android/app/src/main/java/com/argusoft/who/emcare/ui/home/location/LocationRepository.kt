package com.argusoft.who.emcare.ui.home.location

import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.data.remote.UNEXPECTED_INTERNAL_SERVER
import com.argusoft.who.emcare.ui.common.model.Location
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val database: Database,
    private val preference: Preference
) {

    fun getLocations() = flow {
        val list = mutableListOf<Location>()
        preference.getLoggedInUser()?.location?.forEach {
            list.addAll(database.getChildLocations(it.id)!!)
        }
        if(list.isEmpty())
            emit(ApiResponse.ApiError(UNEXPECTED_INTERNAL_SERVER))
        else
            emit(ApiResponse.Success(list))
    }
}