package com.argusoft.who.emcare.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.argusoft.who.emcare.ui.common.model.Location
import com.argusoft.who.emcare.ui.common.model.LoggedInUser

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocations(locations: List<Location>)

    @Query("SELECT * from location WHERE id=:id")
    suspend fun getLocationById(id: Int): Location?

    @Query("WITH RECURSIVE child AS (SELECT * FROM  location WHERE  id = :id UNION SELECT  l.* FROM location l INNER JOIN child s ON s.id =  l.parent)SELECT * FROM child;")
    suspend fun getChildLocations(id: Int?): List<Location>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLoginUser(loginUser: LoggedInUser)

    @Query("SELECT * from loggedinuser WHERE userName=:username AND password=:password")
    suspend fun loginUser(username: String, password: String): LoggedInUser?

    @Query("SELECT * from loggedinuser")
    suspend fun getAllUser(): List<LoggedInUser>?
}