package com.argusoft.who.emcare.data.local.database

import androidx.room.Database
import androidx.room.TypeConverters
import com.argusoft.who.emcare.ui.common.model.Location
import com.argusoft.who.emcare.ui.common.model.LoggedInUser

@Database(entities = [Location::class, LoggedInUser::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoomDatabase : androidx.room.RoomDatabase() {

    abstract fun dao(): Dao
}