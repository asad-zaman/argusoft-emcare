package com.argusoft.who.emcare.ui.home.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.data.remote.UNEXPECTED_INTERNAL_SERVER
import com.argusoft.who.emcare.ui.common.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val database: Database,
    private val preference: Preference
) : ViewModel() {

    private val _locationAndRolesApiState = MutableLiveData<ApiResponse<List<Location>>>()
    val locationAndRolesApiState: LiveData<ApiResponse<List<Location>>> = _locationAndRolesApiState

    var locationId: Int? = null

    init {
        getLocations()
    }

    fun getLocations() {
        _locationAndRolesApiState.value = ApiResponse.Loading()
        viewModelScope.launch {
            preference.getLoggedInUser()?.location?.let {
                val list = database.getChildLocations(it.id)
                _locationAndRolesApiState.value = ApiResponse.Success(data = list)
            } ?: let { _locationAndRolesApiState.value = ApiResponse.ApiError(UNEXPECTED_INTERNAL_SERVER) }
        }
    }
}