package com.argusoft.who.emcare.data.local.database

import androidx.room.*
import androidx.room.Database
import androidx.room.migration.AutoMigrationSpec
import com.argusoft.who.emcare.ui.common.model.*

@Database(entities = [Language::class, LoggedInUser::class, Facility::class, ConsultationFlowItem::class],
    version = 2,
    autoMigrations = [
        AutoMigration (from = 1, to = 2,
            spec = RoomDatabase.RoomAutoMigrationVersion1To2::class)
    ],
    exportSchema = true)
@TypeConverters(Converters::class)
abstract class RoomDatabase : androidx.room.RoomDatabase() {

    class RoomAutoMigrationVersion1To2: AutoMigrationSpec {
    }

    abstract fun dao(): Dao
}