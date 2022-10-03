package com.argusoft.who.emcare.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EmCarePeriodicSyncWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters
) : EmCareSyncWorker(appContext, workerParams) {

    override fun syncData(): SyncTypeParams {
        return listOf(SyncType.FACILITY, SyncType.CONSULTATION_FLOW_ITEM)
    }

}