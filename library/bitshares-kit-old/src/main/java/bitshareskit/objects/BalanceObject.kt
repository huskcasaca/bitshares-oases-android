package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import org.java_json.JSONObject

@Entity(tableName = BalanceObject.TABLE_NAME)
data class BalanceObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    companion object {

        @Ignore const val TABLE_NAME = "balance_object"
//        @Ignore
//        const val KEY = ""
    }

    init {

    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
