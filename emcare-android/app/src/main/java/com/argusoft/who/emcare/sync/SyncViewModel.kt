package com.argusoft.who.emcare.sync

import android.app.Application
import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val api: Api,
    private val database: Database,
    private val preference: Preference,
    private val applicationContext: Application
): ViewModel(){

    private val _syncState = MutableLiveData<ApiResponse<State>>()
    val syncState: LiveData<ApiResponse<State>> = _syncState

    private val formatString24 = "dd/MM/yyyy HH:mm:ss"
    private val formatString12 = "dd/MM/yyyy hh:mm:ss a"
    fun syncPatients() {
        _syncState.value = ApiResponse.Loading(false)
        viewModelScope.launch {
            val fhirResult = Sync.oneTimeSync(
                applicationContext,
                fhirEngine,
                DownloadWorkManagerImpl(preference),
                AcceptRemoteConflictResolver
            )
            val emCareResult = EmCareSync.oneTimeSync(api, database, preference, listOf(SyncType.FACILITY, SyncType.CONSULTATION_FLOW_ITEM))
            if (fhirResult is Result.Success || emCareResult is SyncResult.Success) {
                _syncState.value = (fhirResult as? Result.Success)?.let { ApiResponse.Success(State.Finished(it)) }
                _syncState.value = null
                preference.writeLastSyncTimestamp(OffsetDateTime.now().toLocalDateTime().format(
                    DateTimeFormatter.ofPattern(if (DateFormat.is24HourFormat(applicationContext)) formatString24 else formatString12)))
            } else {
                _syncState.value = (fhirResult as? Result.Error)?.let { ApiResponse.ApiError(apiErrorMessageResId = R.string.msg_sync_failed) }
            }
        }
    }
}