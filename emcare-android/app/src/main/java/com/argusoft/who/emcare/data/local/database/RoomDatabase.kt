package com.argusoft.who.emcare.data.local.database

import androidx.room.Database
import androidx.room.TypeConverters
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.ui.common.model.Facility
import com.argusoft.who.emcare.ui.common.model.Language
import com.argusoft.who.emcare.ui.common.model.LoggedInUser

@Database(entities = [Language::class, LoggedInUser::class, Facility::class, ConsultationFlowItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoomDatabase : androidx.room.RoomDatabase() {

    abstract fun dao(): Dao
}