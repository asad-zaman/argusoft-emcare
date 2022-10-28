package com.argusoft.who.emcare.data.local.database

import com.argusoft.who.emcare.ui.common.model.*

class DatabaseManager(roomDatabase: RoomDatabase) : Database {

    private val dao = roomDatabase.dao()

    override suspend fun saveFacilities(facilities: List<Facility>) {
        dao.saveFacilities(facilities)
    }

    override suspend fun saveLanguages(languages: List<Language>) {
        dao.saveLanguages(languages)
    }

    override suspend fun getAllLanguages(): List<Language>? {
        return dao.getAllLanguages()
    }

    override suspend fun saveLoginUser(loginUser: LoggedInUser) {
        dao.saveLoginUser(loginUser)
    }

    override suspend fun loginUser(username: String, password: String): LoggedInUser? {
        return dao.loginUser(username, password)
    }

    override suspend fun getAllUser(): List<LoggedInUser>? {
        return dao.getAllUser()
    }

    override suspend fun getLastLoggedInUser(): LoggedInUser? {
        return dao.getLastLoggedInUser()
    }

    override suspend fun getLanguageByCode(languageCode: String): Language? {
        return dao.getLanguageByCode(languageCode)
    }

    override suspend fun deleteAllConsultations() {
        dao.deleteAllConsultations()
    }

    override suspend fun saveConsultationFlowItem(consultation: ConsultationFlowItem) {
        dao.saveConsultationFlowItem(consultation)
    }


    override suspend fun saveConsultationFlowItems(consultations: List<ConsultationFlowItem>) {
        dao.saveConsultationFlowItems(consultations)
    }

    override suspend fun updateConsultationQuestionnaireResponseText(
        consultationId: String,
        questionnaireResponseText: String,
        consultationDate: String
    ) {
        dao.updateConsultationQuestionnaireResponseText(consultationId,questionnaireResponseText, consultationDate)
    }

    override suspend fun updateConsultationFlowInactiveByEncounterId(encounterId: String) {
        dao.updateConsultationFlowInactiveByEncounterId(encounterId)
    }

    override suspend fun getAllConsultations() : List<ConsultationFlowItem>? {
        return dao.getAllConsultations()
    }

    override suspend fun getAllConsultationsByEncounterId(encounterId: String): List<ConsultationFlowItem>? {
        return dao.getAllConsultationsByEncounterId(encounterId)
    }

    override suspend fun getAllActiveConsultations(): List<ConsultationFlowItem>? {
        return dao.getAllActiveConsultations()
    }

    override suspend fun getAllLatestActiveConsultations(): List<ConsultationFlowItem>? {
        return dao.getAllLatestActiveConsultations()
    }

    override suspend fun getAllLatestActiveConsultationsByPatientId(patientId: String): List<ConsultationFlowItem>? {
        return dao.getAllLatestActiveConsultationsByPatientId(patientId)
    }

    override suspend fun getAllConsultationsByPatientId(patientId: String): List<ConsultationFlowItem>? {
        return dao.getAllConsultationsByPatientId(patientId)
    }

    override suspend fun getLatestActiveConsultationByPatientId(patientId: String): ConsultationFlowItem? {
        return dao.getLatestActiveConsultationByPatientId(patientId)
    }
}