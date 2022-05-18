package com.argusoft.who.emcare.sync

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.Result
import com.google.android.fhir.sync.State
import com.google.android.fhir.sync.Sync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.ResourceType
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val api: Api,
    private val database: Database,
    private val preference: Preference,
    private val applicationContext: Application
): ViewModel(){

    private val _syncState = MutableLiveData<State>()
    val syncState: LiveData<State> = _syncState

    fun syncPatients() {
        _syncState.value = State.Started
        viewModelScope.launch {
            val fhirResult = Sync.oneTimeSync(
                applicationContext,
                fhirEngine,
                DownloadWorkManagerImpl()
            )
            val emCareResult = EmCareSync.oneTimeSync(api, database, preference, listOf(SyncType.LOCATION, SyncType.LANGUAGE))
            if (fhirResult is Result.Success || emCareResult is SyncResult.Success) {
                _syncState.value = (fhirResult as? Result.Success)?.let { State.Finished(it) }
            } else {
                _syncState.value = (fhirResult as? Result.Error)?.let { State.Failed(it) }
            }
        }
    }
}