package com.argusoft.who.emcare

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.work.Constraints
import com.argusoft.who.emcare.data.FhirPeriodicSyncWorker
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.RepeatInterval
import com.google.android.fhir.sync.State
import com.google.android.fhir.sync.Sync
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow

/** View model for [MainActivity]. */
class MainActivityViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  val lastSyncLiveData = MutableLiveData<String>()

  private val job = Sync.basicSyncJob(application.applicationContext)

  /** Requests periodic sync. */
  fun poll(): Flow<State> {
    return job.poll(
      PeriodicSyncConfiguration(
        syncConstraints = Constraints.Builder().build(),
        repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES)
      ),
      FhirPeriodicSyncWorker::class.java
    )
  }

  /** Emits last sync time. */
  fun getLastSyncTime() {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    lastSyncLiveData.value = job.lastSyncTimestamp()?.toLocalDateTime()?.format(formatter) ?: ""
  }
}
