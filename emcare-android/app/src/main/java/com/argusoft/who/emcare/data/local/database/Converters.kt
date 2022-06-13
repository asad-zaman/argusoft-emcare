package com.argusoft.who.emcare.data.local.database

import androidx.room.TypeConverter
import com.argusoft.who.emcare.ui.common.model.Location
import com.argusoft.who.emcare.ui.common.model.LoggedInUser
import com.argusoft.who.emcare.utils.extention.fromJsonArray
import com.argusoft.who.emcare.utils.extention.toJson
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun restoreList(listOfString: String?): List<String>? {
        return listOfString?.fromJsonArray()
    }

    @TypeConverter
    fun saveList(listOfString: List<String>?): String {
        return listOfString.toJson()
    }

    @TypeConverter
    fun listToJson(value: List<Location>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Location>::class.java).toList()

    @TypeConverter
    fun listToJson1(value: List<LoggedInUser.Feature>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList1(value: String) = Gson().fromJson(value, Array<LoggedInUser.Feature>::class.java).toList()
}