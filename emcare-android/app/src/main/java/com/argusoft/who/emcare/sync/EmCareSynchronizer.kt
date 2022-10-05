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

import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.utils.extention.whenFailed
import com.argusoft.who.emcare.utils.extention.whenSuccess
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

sealed class SyncResult {
//    val timestamp: OffsetDateTime = OffsetDateTime.now()

    object Success : SyncResult()
    data class Error(val exceptions: List<SyncException>) : SyncResult()
}

data class SyncException(val syncType: SyncType)

typealias SyncTypeParams = List<SyncType>

sealed class SyncState {
    object Started : SyncState()

    data class InProgress(val resourceType: SyncType?) : SyncState()
    data class Glitch(val exceptions: List<SyncException>) : SyncState()

    data class Finished(val result: SyncResult.Success) : SyncState()
    data class Failed(val result: SyncResult.Error) : SyncState()
}

enum class SyncType {
    FACILITY,
    LANGUAGE,
    CONSULTATION_FLOW_ITEM,
}

/** Class that helps synchronize the data source and save it in the local database */
internal class EmCareSynchronizer(
    private val api: Api,
    private val database: Database,
    private val preference: Preference,
    private val syncTypeParams: SyncTypeParams
) {
    private var flow: MutableSharedFlow<SyncState>? = null

    private fun isSubscribed(): Boolean {
        return flow != null
    }

    fun subscribe(flow: MutableSharedFlow<SyncState>) {
        if (isSubscribed()) {
            throw IllegalStateException("Already subscribed to a flow")
        }

        this.flow = flow
    }

    private suspend fun emit(syncState: SyncState) {
        flow?.emit(syncState)
    }

    private suspend fun emitResult(result: SyncResult): SyncResult {
//        preference.writeLastSyncTimestamp(result.timestamp.toString())

        when (result) {
            is SyncResult.Success -> emit(SyncState.Finished(result))
            is SyncResult.Error -> emit(SyncState.Failed(result))
        }

        return result
    }

    suspend fun synchronize(): SyncResult {
        emit(SyncState.Started)

        return listOf(upload(), download())
            .filterIsInstance<SyncResult.Error>()
            .flatMap { it.exceptions }
            .let {
                if (it.isEmpty()) {
                    emitResult(SyncResult.Success)
                } else {
                    emitResult(SyncResult.Error(it))
                }
            }
    }

    private suspend fun download(): SyncResult {
        val exceptions = mutableListOf<SyncException>()
        syncTypeParams.forEach { syncType ->
            emit(SyncState.InProgress(syncType))
            when (syncType) {
                SyncType.FACILITY -> {
                    val facility = api.getFacilities()
                    facility.whenSuccess {
                        database.saveFacilities(it)
                    }
                    facility.whenFailed {
                        exceptions.add(SyncException(syncType))
                    }
                }
                SyncType.LANGUAGE -> {
                    val language = api.getLanguages()
                    language.whenSuccess {
                        database.saveLanguages(it)
                    }
                    language.whenFailed {
                        exceptions.add(SyncException(syncType))
                    }
                }
                SyncType.CONSULTATION_FLOW_ITEM -> {
                    val consultations = api.getConsultationFlow()
                    consultations.whenSuccess {
                        it.forEach {
                            consultationFlowItem ->
                            if(consultationFlowItem.consultationDate != null) {
                                consultationFlowItem.consultationDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(consultationFlowItem.consultationDate!!.toLong()), ZoneId.of("UTC")).toOffsetDateTime().toString().removeSuffix("Z")
                            }
                        }
                        database.saveConsultationFlowItems(it)
                    }
                    consultations.whenFailed {
                        exceptions.add(SyncException(syncType))
                    }
                }
            }
        }
        return if (exceptions.isEmpty()) {
            SyncResult.Success
        } else {
            emit(SyncState.Glitch(exceptions))

            SyncResult.Error(exceptions)
        }
    }

    private suspend fun upload(): SyncResult {
        val exceptions = mutableListOf<SyncException>()

        syncTypeParams.forEach { syncType ->
            emit(SyncState.InProgress(syncType))
            if (syncType == SyncType.CONSULTATION_FLOW_ITEM) {
                val consultationsList = database.getAllConsultations()
                if(!consultationsList.isNullOrEmpty()) {
                    val consultations = api.saveConsultations(consultationsList)
                    consultations.whenFailed {
                        exceptions.add(SyncException(syncType))
                    }
                }

            }
        }

        return if (exceptions.isEmpty()) {
            SyncResult.Success
        } else {
            emit(SyncState.Glitch(exceptions))

            SyncResult.Error(exceptions)
        }
    }
}
