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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _locationAndRolesApiState = MutableLiveData<ApiResponse<List<Location>>>()
    val locationAndRolesApiState: LiveData<ApiResponse<List<Location>>> = _locationAndRolesApiState

    var locationId: Int? = null

    init {
        getLocations()
    }

    private fun getLocations() {
        _locationAndRolesApiState.value = ApiResponse.Loading()
        viewModelScope.launch {
            locationRepository.getLocations().collect {
                _locationAndRolesApiState.value = it
            }
        }
    }
}