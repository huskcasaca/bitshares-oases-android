package com.bitshares.oases.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import bitshareskit.models.Ticker
import bitshareskit.models.decodeMarketInstance
import org.java_json.JSONObject


@Entity(tableName = TickerEntity.TABLE_NAME)
data class TickerEntity(
    @ColumnInfo(name = COLUMN_DATA) val rawJson: JSONObject,
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = COLUMN_UID) val instance: Long
) {

    companion object {
        const val TABLE_NAME = "ticker"

        const val COLUMN_DATA = "data"
        const val COLUMN_UID = "uid"
    }

    val ticker get() = Ticker.fromJson(rawJson, decodeMarketInstance(instance).first, decodeMarketInstance(instance).second)

}

