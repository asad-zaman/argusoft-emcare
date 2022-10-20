package com.argusoft.who.emcare.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.ui.common.model.Facility
import com.argusoft.who.emcare.ui.common.model.Language
import com.argusoft.who.emcare.ui.common.model.LoggedInUser

@Dao
interface Dao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFacilities(facilities: List<Facility>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLanguages(languages: List<Language>)

    @Query("SELECT * from language")
    suspend fun getAllLanguages(): List<Language>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLoginUser(loginUser: LoggedInUser)

    @Query("SELECT * from loggedinuser WHERE userName=:username AND password=:password")
    suspend fun loginUser(username: String, password: String): LoggedInUser?

    @Query("SELECT * from loggedinuser")
    suspend fun getAllUser(): List<LoggedInUser>?

    @Query("SELECT * from loggedinuser ORDER BY loginTime DESC LIMIT 1 ")
    suspend fun getLastLoggedInUser(): LoggedInUser?

    @Query("SELECT * from language where languageCode=:languageCode")
    suspend fun getLanguageByCode(languageCode: String): Language?

    @Query("DELETE from consultationflowitem")
    suspend fun deleteAllConsultations()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConsultationFlowItems(consultations: List<ConsultationFlowItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConsultationFlowItem(consultation: ConsultationFlowItem)

    @Query("UPDATE ConsultationFlowItem SET questionnaireResponseText=:questionnaireResponseText, consultationDate=:consultationDate WHERE id=:consultationId")
    suspend fun updateConsultationQuestionnaireResponseText(consultationId: String, questionnaireResponseText: String, consultationDate: String)

    @Query("UPDATE ConsultationFlowItem SET isActive=0 WHERE encounterId=:encounterId")
    suspend fun updateConsultationFlowInactiveByEncounterId(encounterId: String)

    @Query("SELECT * from consultationflowitem")
    suspend fun getAllConsultations(): List<ConsultationFlowItem>?

    @Query("SELECT * from consultationflowitem WHERE encounterId=:encounterId ORDER BY consultationDate DESC")
    suspend fun getAllConsultationsByEncounterId(encounterId: String): List<ConsultationFlowItem>?

    @Query("SELECT * from consultationflowitem where isActive=1")
    suspend fun getAllActiveConsultations(): List<ConsultationFlowItem>?

    @Query("SELECT * from consultationflowitem where isActive=1 AND consultationDate in (SELECT MAX(consultationDate) from consultationflowitem group by encounterId) ORDER BY consultationDate DESC")
    suspend fun getAllLatestActiveConsultations(): List<ConsultationFlowItem>?

    @Query("SELECT * from consultationflowitem where isActive=1 AND patientId=:patientId")
    suspend fun getAllActiveConsultationsByPatientId(patientId: String): List<ConsultationFlowItem>?

    @Query("SELECT * from consultationflowitem where isActive=1 AND patientId=:patientId ORDER BY consultationDate DESC LIMIT 1")
    suspend fun getLatestActiveConsultationByPatientId(patientId: String): ConsultationFlowItem?

    @Query("SELECT * from consultationflowitem where patientId=:patientId")
    suspend fun getAllConsultationsByPatientId(patientId: String): List<ConsultationFlowItem>?

}