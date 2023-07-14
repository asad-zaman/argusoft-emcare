package com.argusoft.who.emcare.sync

import android.app.Application
import android.util.Log
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
import com.google.android.fhir.sync.Sync
import com.google.android.fhir.sync.SyncJobStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
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
    private var isProgressAlreadyStarted = false
    private var isFinished = false
    private var lastProgress: Int = 0
    private val timeDelay: Long = 1000
    private var diff: Int = 5
    private var progress: Int = 0
    private var progressOriginal: Int = 0

    fun syncPatients(isRefresh: Boolean) {
        Log.d("Sync Called","Inside SyncPatients")
        isFinished = false
        _syncState.value = ApiResponse.Loading(isRefresh)
        viewModelScope.launch {
            lastSyncTime = OffsetDateTime.now().toLocalDateTime().format(
                DateTimeFormatter.ofPattern(formatString12))
            progress = 0
            lastProgress = 0
            val emCareResult = EmCareSync.oneTimeSync(api, database, preference, listOf(SyncType.FACILITY, SyncType.CONSULTATION_FLOW_ITEM))
            Sync.oneTimeSync<com.argusoft.who.emcare.sync.FhirSyncWorker>(
                applicationContext
            ).shareIn(this, SharingStarted.Eagerly, 10)
                .collect { syncJobStatus ->
                    Executors.newSingleThreadScheduledExecutor().schedule({
                        //blank body
                    }, 1, TimeUnit.SECONDS)
                    if (syncJobStatus is SyncJobStatus.Finished /*&& emCareResult is SyncResult.Success*/) {
                        _syncState.value = ApiResponse.Success(syncJobStatus)
                        preference.writeLastSyncTimestamp(lastSyncTime)
                    } else if (syncJobStatus is SyncJobStatus.InProgress) {
                        progress = syncJobStatus.completed.toDouble().div(syncJobStatus.total)
                            .times(100).toInt()
//                        if(syncJobStatus.total == 0)
//                            preference.writeLastSyncTimestamp(lastSyncTime)

                        // fhir auto sync progress
                        autoSyncProgress(syncJobStatus)

                        // for smooth manual transition
//                        manualSyncProgress(syncJobStatus)

                    } else {
                        _syncState.value = (syncJobStatus is SyncJobStatus.Failed).let {
                            ApiResponse.ApiError(apiErrorMessageResId = R.string.msg_sync_failed)
                        }
                    }
            }
        }
    }

    private fun autoSyncProgress(syncJobStatus : SyncJobStatus.InProgress){
        if(!isFinished)
            _syncState.value = ApiResponse.InProgress(
                syncJobStatus.total,
                progressCount = progress
            )
        isFinished = syncJobStatus.total == 0

//        if (progress >= 100) {
//            preference.writeLastSyncTimestamp(lastSyncTime)
//        }
    }

    private fun manualSyncProgress(syncJobStatus : SyncJobStatus.InProgress){
        progressOriginal = progress
        if (!isProgressAlreadyStarted) {
            viewModelScope.launch {
                while (lastProgress <= 100) {
                    if (syncJobStatus.total == 0) {
                        _syncState.value = ApiResponse.InProgress(
                            total = 0,
                            progressCount = 0
                        )
                        break
                    }
                    if (progress == 0) {
                        progress = 1
                        lastProgress = 1
                    }
//
                    if (progress < lastProgress)
                        progress = lastProgress
                    else if ((progress - lastProgress) > diff)
                        progress = lastProgress + (diff - (lastProgress % diff))
                    else
                        progress += (diff - (progress % diff))

//                   if (progress >= 100 && lastProgress < 99)
//                       progress = 99

                    if (progress >= 100 && progressOriginal < 100) {
                        progress = 99
                    }
                    _syncState.value = ApiResponse.InProgress(
                        syncJobStatus.total,
                        progressCount = progress
                    )
                    if (progress >= 100 && progressOriginal >= 100) {
                        preference.writeLastSyncTimestamp(lastSyncTime)
                        break
                    }
                    lastProgress = progress

                    delay(timeDelay)

                }
            }
            isProgressAlreadyStarted = true
        }
    }
}