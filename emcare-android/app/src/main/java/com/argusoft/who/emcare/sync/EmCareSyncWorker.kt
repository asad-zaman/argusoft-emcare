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
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.argusoft.who.emcare.EmCareApplication
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@PublishedApi internal const val MAX_RETRIES_ALLOWED = "max_retires"
/** A WorkManager Worker that handles periodic sync. */
abstract class EmCareSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    private val TAG = javaClass.name

    abstract fun syncData(): SyncTypeParams

    private lateinit var fhirSynchronizer: EmCareSynchronizer

    override suspend fun doWork(): Result {
        val emCareApplication = applicationContext as EmCareApplication
        fhirSynchronizer =   EmCareSynchronizer(emCareApplication.api, emCareApplication.database, emCareApplication.preference, syncData())
        val flow = MutableSharedFlow<SyncState>()
        val job =
            CoroutineScope(Dispatchers.IO).launch {
                flow.collect {
                    // now send Progress to work manager so caller app can listen
                    setProgress(buildWorkData(it))

                    if (it is SyncState.Finished || it is SyncState.Failed) {
                        this@launch.cancel()
                    }
                }
            }

        fhirSynchronizer.subscribe(flow)

        Log.v(TAG, "Subscribed to flow for progress")

        val result = fhirSynchronizer.synchronize()
        val output = buildOutput(result)

        // await/join is needed to collect states completely
        kotlin.runCatching { job.join() }.onFailure { Log.w(TAG, it) }

        setProgress(output)

        Log.d(TAG, "Received result from worker $result and sending output $output")

        /**
         * In case of failure, we can check if its worth retrying and do retry based on
         * [RetryConfiguration.maxRetries] set by user.
         */
        val retries = inputData.getInt(MAX_RETRIES_ALLOWED, 0)
        return when {
            result is SyncResult.Success -> {
                Result.success(output)
            }
            retries > runAttemptCount -> {
                Result.retry()
            }
            else -> {
                Result.failure(output)
            }
        }
    }

    private fun buildOutput(result: SyncResult): Data {
        return when (result) {
            is SyncResult.Success -> buildWorkData(SyncState.Finished(result))
            is SyncResult.Error -> buildWorkData(SyncState.Failed(result))
        }
    }

    private fun buildWorkData(state: SyncState): Data {
        return workDataOf(
            // send serialized state and type so that consumer can convert it back
            "StateType" to state::class.java.name,
            "State" to state.toString()
        )
    }

    /**
     * Exclusion strategy for [Gson] that handles field exclusions for [State] returned by
     * FhirSynchronizer. It should skip serializing the exceptions to avoid exceeding WorkManager
     * WorkData limit
     * @see <a
     * href="https://github.com/google/android-fhir/issues/707">https://github.com/google/android-fhir/issues/707</a>
     */
    internal class StateExclusionStrategy : ExclusionStrategy {
        override fun shouldSkipField(field: FieldAttributes) = field.name.equals("exceptions")

        override fun shouldSkipClass(clazz: Class<*>?) = false
    }
}
