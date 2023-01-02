package com.argusoft.who.emcare.data.local.database

import com.argusoft.who.emcare.ui.common.model.*

interface Database {
    suspend fun saveFacilities(facilities: List<Facility>)

    suspend fun saveLanguages(languages: List<Language>)

    suspend fun getAllLanguages(): List<Language>?

    suspend fun saveLoginUser(loginUser: LoggedInUser)

    suspend fun loginUser(username: String, password: String): LoggedInUser?

    suspend fun getAllUser(): List<LoggedInUser>?

    suspend fun getLastLoggedInUser(): LoggedInUser?

    suspend fun getLanguageByCode(languageCode: String): Language?

    suspend fun deleteAllConsultations()

    suspend fun saveConsultationFlowItem(consultation: ConsultationFlowItem)

    suspend fun saveConsultationFlowItems(consultations: List<ConsultationFlowItem>)

    suspend fun updateConsultationQuestionnaireResponseText(consultationId: String, questionnaireResponseText: String, consultationDate: String)

    suspend fun updateConsultationFlowInactiveByEncounterId(encounterId: String)

    suspend fun getAllConsultations() : List<ConsultationFlowItem>?

    suspend fun getAllConsultationsByEncounterId(encounterId: String): List<ConsultationFlowItem>?

    suspend fun getNextConsultationByConsultationIdAndEncounterId(consultationId: String, encounterId: String): ConsultationFlowItem?

    suspend fun getAllActiveConsultations(): List<ConsultationFlowItem>?

    suspend fun getAllLatestActiveConsultations(): List<ConsultationFlowItem>?

    suspend fun getAllLatestActiveConsultationsByPatientId(patientId: String): List<ConsultationFlowItem>?

    suspend fun getAllLatestInActiveConsultationsByPatientId(patientId: String): List<ConsultationFlowItem>?

    suspend fun getAllConsultationsByPatientId(patientId: String): List<ConsultationFlowItem>?

    suspend fun getLatestActiveConsultationByPatientId(patientId: String): ConsultationFlowItem?

    suspend fun getLastConsultationDateByPatientId(patientId: String): String?

    suspend fun deleteNextConsultations(consultationFlowItemId: String, encounterId: String)

    suspend fun getConsultationFlowItemById(consultationFlowItemId: String): ConsultationFlowItem?

    suspend fun getNextConsultationFlowItemIds(consultationFlowItemId: String, encounterId: String): List<String>

    suspend fun deleteConsultationFlowItemById(consultationFlowItemId: String)
}