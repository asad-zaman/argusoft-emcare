package com.argusoft.who.emcare.ui.home

import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.google.android.fhir.FhirEngine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConsultationFlowRepository @Inject constructor(
    private val database: Database,
    private val fhirEngine: FhirEngine
) {

    fun getAllActiveConsultations() = flow {
        val list = database.getAllActiveConsultations()
        emit(ApiResponse.Success(data = list))
    }

    fun saveConsultation(consultation: ConsultationFlowItem) = flow {
        database.saveConsultationFlowItem(consultation)
        emit(ApiResponse.Success(data = consultation))
    }

    fun updateConsultationQuestionnaireResponseText(consultationId: String, questionnaireResponseText: String, consultationDate: String) = flow {
        database.updateConsultationQuestionnaireResponseText(consultationId, questionnaireResponseText, consultationDate)
        emit(ApiResponse.Success(data=consultationId))
    }

    fun updateConsultationFlowInactiveByEncounterId(encounterId: String) = flow {
        database.updateConsultationFlowInactiveByEncounterId(encounterId)
        emit(ApiResponse.Success(data=encounterId))
    }

    //For the Consultation List
    fun getAllLatestActiveConsultations() = flow {
        val list = database.getAllLatestActiveConsultations()
        emit(ApiResponse.Success(data = list))
    }

    fun getAllLatestActiveConsultationsPaginated(limit: Int, offset: Int) = flow {
        val list = database.getAllLatestActiveConsultationsPaginated(limit, offset)
        emit(ApiResponse.Success(data = list))
    }

    fun getAllConsultationsByEncounterId(encounterId: String) = flow {
        val list = database.getAllConsultationsByEncounterId(encounterId)
        emit(ApiResponse.Success(data = list))
    }

    fun getNextConsultationByConsultationIdAndEncounterId(consultationId: String, encounterId: String) = flow {
        val list = database.getNextConsultationByConsultationIdAndEncounterId(consultationId, encounterId)
        emit(ApiResponse.Success(data = list))
    }

    //For the consultation flow screen
    fun getAllLatestActiveConsultationsByPatientId(patientId: String) = flow {
        val list = database.getAllLatestActiveConsultationsByPatientId(patientId)
        emit(ApiResponse.Success(data = list))
    }

    fun getAllLatestInActiveConsultationsByPatientId(patientId: String) = flow {
        val list = database.getAllLatestInActiveConsultationsByPatientId(patientId)
        emit(ApiResponse.Success(data = list))
    }

    fun getConsultationSyncState(consultationItem:ConsultationFlowItem) = flow {
        var isSynced = true
        val localChangesList = fhirEngine.getAllLocalChanges()
        consultationItem.encounterId.let { encounterId ->
            for (localChange in localChangesList) {
                localChange?.let {
                    isSynced = !(it.payload.contains(encounterId))
                }
                if(!isSynced)
                    break
            }
        }
        emit(ApiResponse.Success(data = isSynced))
    }

    //For the Patient Profile
    fun getAllConsultationsByPatientId(patientId: String) = flow {
        val list = database.getAllConsultationsByPatientId(patientId)
        emit(ApiResponse.Success(data = list))
    }

    fun getLastConsultationDateByPatientId(patientId: String) = flow {
        val lastConsultationDate = database.getLastConsultationDateByPatientId(patientId)
        emit(ApiResponse.Success(data = lastConsultationDate))
    }

    fun deleteNextConsultations(consultationFlowItemId: String, encounterId: String) = flow {
        database.deleteNextConsultations(consultationFlowItemId, encounterId)
        emit(ApiResponse.Success(data = "Deleted"))
    }

    fun getConsultationFLowItemById(consultationFlowItemId: String) = flow {
        val consultationFlowItem = database.getConsultationFlowItemById(consultationFlowItemId)
        emit(ApiResponse.Success(data = consultationFlowItem))
    }

    fun deleteConsultationFlowItemById(consultationFlowItemId: String) = flow {
        database.deleteConsultationFlowItemById(consultationFlowItemId)
        emit(ApiResponse.Success(data = "Deleted"))
    }

    fun getNextConsultationFlowItemIds(consultationFlowItemId: String, encounterId: String) = flow {
        val consultationFlowItemIds = database.getNextConsultationFlowItemIds(consultationFlowItemId, encounterId)
        emit(ApiResponse.Success(data = consultationFlowItemIds))
    }

    fun getConsultationCountAfterTimestamp(timestamp: String) = flow {
        val count = database.getConsultationCountAfterTimestamp(timestamp)
        emit(ApiResponse.Success(data = count))
    }

    fun getConsultationFlowItemsForReview(encounterId: String) = flow {
        val consultations = database.getConsultationFlowItemsForReview(encounterId)
        emit(ApiResponse.Success(data = consultations))
    }

}