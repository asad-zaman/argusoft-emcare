package com.argusoft.who.emcare.data.local.database

import androidx.room.TypeConverter
import com.argusoft.who.emcare.utils.extention.fromJsonArray
import com.argusoft.who.emcare.utils.extention.toJson

class Converters {

    @TypeConverter
    fun restoreList(listOfString: String?): List<String>? {
        return listOfString?.fromJsonArray()
    }

    @TypeConverter
    fun saveList(listOfString: List<String>?): String {
        return listOfString.toJson()
    }
}