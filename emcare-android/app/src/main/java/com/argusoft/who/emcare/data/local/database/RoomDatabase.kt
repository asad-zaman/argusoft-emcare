package com.argusoft.who.emcare.data.local.database

import androidx.room.Database
import com.argusoft.who.emcare.data.local.database.Dao
import com.argusoft.who.emcare.ui.common.model.Album

@Database(entities = [Album::class], version = 1, exportSchema = false)
abstract class RoomDatabase : androidx.room.RoomDatabase() {

    abstract fun dao(): Dao
}