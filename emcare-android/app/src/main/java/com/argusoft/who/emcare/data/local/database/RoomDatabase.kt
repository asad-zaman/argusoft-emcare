package com.argusoft.who.emcare.data.local.database

import androidx.room.Database

//@Database(entities = [], version = 1, exportSchema = false)
abstract class RoomDatabase : androidx.room.RoomDatabase() {

    abstract fun dao(): Dao
}