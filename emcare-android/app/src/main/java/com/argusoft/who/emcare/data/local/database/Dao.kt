package com.argusoft.who.emcare.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("SELECT * from language where languageCode=:languageCode")
    suspend fun getLanguageByCode(languageCode: String): Language?

}