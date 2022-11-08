package com.argusoft.who.emcare.ui.home

import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConsultationFlowRepository @Inject constructor(
    private val database: Database
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

    fun getAllConsultationsByEncounterId(encounterId: String) = flow {
        val list = database.getAllConsultationsByEncounterId(encounterId)
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

    //For the Patient Profile
    fun getAllConsultationsByPatientId(patientId: String) = flow {
        val list = database.getAllConsultationsByPatientId(patientId)
        emit(ApiResponse.Success(data = list))
    }

    fun getLastConsultationDateByPatientId(patientId: String) = flow {
        val lastConsultationDate = database.getLastConsultationDateByPatientId(patientId)
        emit(ApiResponse.Success(data = lastConsultationDate))
    }


}