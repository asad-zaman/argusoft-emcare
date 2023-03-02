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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val api: Api,
    private val database: Database,
    private val preference: Preference,
    private val applicationContext: Application
): ViewModel(){

    private val _syncState = MutableLiveData<ApiResponse<SyncJobStatus>>()
    val syncState: LiveData<ApiResponse<SyncJobStatus>> = _syncState

    private val formatString12 = "dd/MM/yyyy hh:mm:ss a"
    fun syncPatients() {
        _syncState.value = ApiResponse.Loading(false)
        viewModelScope.launch {
            val emCareResult = EmCareSync.oneTimeSync(api, database, preference, listOf(SyncType.FACILITY, SyncType.CONSULTATION_FLOW_ITEM))
            Sync.oneTimeSync<com.argusoft.who.emcare.sync.FhirSyncWorker>(
                applicationContext
            ).shareIn(this, SharingStarted.Eagerly, 10)
                .collect { syncJobStatus ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    //blank body
                }, 1, TimeUnit.SECONDS)
                if (syncJobStatus is SyncJobStatus.Finished || emCareResult is SyncResult.Success) {
                    _syncState.value = (syncJobStatus is SyncJobStatus.Finished)?.let { ApiResponse.Success(syncJobStatus) }
                    _syncState.value = null
                    preference.writeLastSyncTimestamp(OffsetDateTime.now().toLocalDateTime().format(
                        DateTimeFormatter.ofPattern(formatString12)))
                } else if( syncJobStatus is SyncJobStatus.InProgress) {
                    _syncState.value = ApiResponse.Success(syncJobStatus)
                    _syncState.value = null

                } else {
                    _syncState.value = (syncJobStatus is SyncJobStatus.Failed)?.let { ApiResponse.ApiError(apiErrorMessageResId = R.string.msg_sync_failed) }
                }
            }

        }
    }
}