package com.bitshares.oases.database.converters

import androidx.room.TypeConverter
import org.java_json.JSONArray

class BooleanListConverters {

    @TypeConverter
    fun fromBooleanList(list: List<Boolean>): String = JSONArray(list).toString()

    @TypeConverter
    fun toKeyList(message: String?): List<Boolean> = JSONArray(message).map { it as Boolean }

}
