/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.argusoft.who.emcare.sync

import android.content.Context
import androidx.work.*
import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.RetryConfiguration
import com.google.android.fhir.sync.defaultRetryConfiguration

object EmCareSync {

suspend fun oneTimeSync(
        api: Api,
        database: Database,
        preference: Preference,
        syncTypeParams: SyncTypeParams
    ): SyncResult {
        return EmCareSynchronizer(api, database, preference, syncTypeParams).synchronize()
    }


    inline fun <reified W : EmCareSyncWorker> oneTimeSync(
        context: Context,
        retryConfiguration: RetryConfiguration? = defaultRetryConfiguration
    ) {
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                EmCareSyncWorkType.DOWNLOAD.workerName,
                ExistingWorkPolicy.KEEP,
                createOneTimeWorkRequest<W>(retryConfiguration)
            )
    }


    inline fun <reified W : EmCareSyncWorker> periodicSync(
        context: Context,
        periodicSyncConfiguration: PeriodicSyncConfiguration,
    ) {

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                EmCareSyncWorkType.DOWNLOAD.workerName,
                ExistingPeriodicWorkPolicy.KEEP,
                createPeriodicWorkRequest<W>(periodicSyncConfiguration)
            )
    }

    @PublishedApi
    internal inline fun <reified W : EmCareSyncWorker> createOneTimeWorkRequest(
        retryConfiguration: RetryConfiguration?
    ): OneTimeWorkRequest {
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<W>()
        retryConfiguration?.let {
            oneTimeWorkRequest.setBackoffCriteria(
                it.backoffCriteria.backoffPolicy,
                it.backoffCriteria.backoffDelay,
                it.backoffCriteria.timeUnit
            )
            oneTimeWorkRequest.setInputData(
                Data.Builder().putInt(MAX_RETRIES_ALLOWED, it.maxRetries).build()
            )
        }
        return oneTimeWorkRequest.build()
    }

    @PublishedApi
    internal inline fun <reified W : EmCareSyncWorker> createPeriodicWorkRequest(
        periodicSyncConfiguration: PeriodicSyncConfiguration
    ): PeriodicWorkRequest {
        val periodicWorkRequestBuilder =
            PeriodicWorkRequestBuilder<W>(
                periodicSyncConfiguration.repeat.interval,
                periodicSyncConfiguration.repeat.timeUnit
            )
                .setConstraints(periodicSyncConfiguration.syncConstraints)

        periodicSyncConfiguration.retryConfiguration?.let {
            periodicWorkRequestBuilder.setBackoffCriteria(
                it.backoffCriteria.backoffPolicy,
                it.backoffCriteria.backoffDelay,
                it.backoffCriteria.timeUnit
            )
            periodicWorkRequestBuilder.setInputData(
                Data.Builder().putInt(MAX_RETRIES_ALLOWED, it.maxRetries).build()
            )
        }
        return periodicWorkRequestBuilder.build()
    }
}

/** Defines different types of synchronisation workers: download and upload */
enum class EmCareSyncWorkType(val workerName: String) {
    DOWNLOAD_UPLOAD("emcare-download-upload-worker"),
    DOWNLOAD("emcare-download"),
    UPLOAD("emcare-upload")
}
