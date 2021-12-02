package com.argusoft.who.emcare.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.Album
import com.argusoft.who.emcare.utils.extention.whenSuccess
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: Api,
    private val preference: Preference,
    private val database: Database
) : ViewModel() {

    private val _apiState = SingleLiveEvent<ApiResponse<List<Album>>>()
    val apiState: LiveData<ApiResponse<List<Album>>>
        get() = _apiState
    private var page = 1
    private var job: Job? = null

    fun getRepository(isRefresh: Boolean = false, isLoadMore: Boolean = false) {
        _apiState.value = ApiResponse.Loading(isRefresh, isLoadMore)
        if (isLoadMore) page++ else page = 1
        job = viewModelScope.launch {
            _apiState.value = api.getRepository(page).whenSuccess {

            }
        }
    }
}