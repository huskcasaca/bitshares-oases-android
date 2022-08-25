package bitshareskit.objects

import androidx.room.Entity
import androidx.room.Ignore
import org.java_json.JSONObject

import androidx.room.ColumnInfo
@Entity(tableName = AccountTransactionHistoryObject.TABLE_NAME)
data class AccountTransactionHistoryObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    companion object {
        @Ignore const val TABLE_NAME = "account_transaction_history_object"
    }

    init {

    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
