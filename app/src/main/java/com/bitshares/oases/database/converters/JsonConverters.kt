package com.bitshares.oases.database.converters

import androidx.room.TypeConverter
import org.java_json.JSONObject

class JsonConverters {
    @TypeConverter
    fun toString(rawJson: JSONObject): String {
        return rawJson.toString()
    }

    @TypeConverter
    fun toJSONArray(data: String): JSONObject {
        return JSONObject(data)
    }
}
