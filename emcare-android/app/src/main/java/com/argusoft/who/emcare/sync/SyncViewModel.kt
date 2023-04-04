package com.argusoft.who.emcare.sync

import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
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
    private lateinit var lastSyncTime: String
    private var isFinished = false

    fun syncPatients(isRefresh: Boolean) {
        isFinished = false
        _syncState.value = ApiResponse.Loading(isRefresh)
        viewModelScope.launch {
            lastSyncTime = OffsetDateTime.now().toLocalDateTime().format(
                DateTimeFormatter.ofPattern(formatString12))
            val emCareResult = EmCareSync.oneTimeSync(api, database, preference, listOf(SyncType.FACILITY, SyncType.CONSULTATION_FLOW_ITEM))
            Sync.oneTimeSync<com.argusoft.who.emcare.sync.FhirSyncWorker>(
                applicationContext
            ).shareIn(this, SharingStarted.Eagerly, 10)
                .collect { syncJobStatus ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    //blank body
                }, 1, TimeUnit.SECONDS)
                    Log.d("syncJobStatus",syncJobStatus.toString())
                if (syncJobStatus is SyncJobStatus.Finished && emCareResult is SyncResult.Success) {
                    _syncState.value = (syncJobStatus is SyncJobStatus.Finished)?.let { ApiResponse.Success(syncJobStatus) }
                    _syncState.value = ApiResponse.Success(null)
                    preference.writeLastSyncTimestamp(lastSyncTime)
                } else if(syncJobStatus is SyncJobStatus.InProgress) {
                    if(!isFinished) {
                        _syncState.value = ApiResponse.InProgress(
                            total = syncJobStatus.total,
                            completed = syncJobStatus.completed
                        )
                        if (syncJobStatus.total == syncJobStatus.completed) {
                            isFinished = true
                            _syncState.value = ApiResponse.Success(null)
                            if(syncJobStatus.total != 0)
                                preference.writeLastSyncTimestamp(lastSyncTime)
                        }
                    }

                } else {
                    _syncState.value = (syncJobStatus is SyncJobStatus.Failed)?.let { ApiResponse.ApiError(apiErrorMessageResId = R.string.msg_sync_failed) }
                }
            }
        }
    }
}