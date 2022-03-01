package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import org.java_json.JSONObject

@Entity(tableName = CustomObject.TABLE_NAME)
data class CustomObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    companion object {
        @Ignore const val TABLE_NAME = "custom_object"
    }

    init {

    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
