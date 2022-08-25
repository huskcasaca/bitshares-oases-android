package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optGrapheneTime
import org.java_json.JSONObject
import java.util.*

@Entity(tableName = AccountStatisticsObject.TABLE_NAME)
data class AccountStatisticsObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    companion object {
        @Ignore const val TABLE_NAME = "account_statistics_object"
        @Ignore const val KEY_OWNER = "owner"
        @Ignore const val KEY_NAME = "name"
        @Ignore const val KEY_MOST_RECENT_OP = "most_recent_op"
        @Ignore const val KEY_TOTAL_OPS = "total_ops"
        @Ignore const val KEY_REMOVED_OPS = "removed_ops"
        @Ignore const val KEY_TOTAL_CORE_IN_ORDERS = "total_core_in_orders"
        @Ignore const val KEY_CORE_IN_BALANCE = "core_in_balance"
        @Ignore const val KEY_HAS_CASHBACK_VB = "has_cashback_vb"
        @Ignore const val KEY_IS_VOTING = "is_voting"
        @Ignore const val KEY_LAST_VOTE_TIME = "last_vote_time"
        @Ignore const val KEY_LIFETIME_FEES_PAID = "lifetime_fees_paid"
        @Ignore const val KEY_PENDING_FEES = "pending_fees"
        @Ignore const val KEY_PENDING_VESTED_FEES = "pending_vested_fees"
/*
	{
		"id": "2.6.96393",
		"owner": "1.2.96393",
		"name": "openledger",
		"most_recent_op": "2.9.1096499868",
		"total_ops": 248077,
		"removed_ops": 247477,
		"total_core_in_orders": 0,
		"core_in_balance": 117321853,
		"has_cashback_vb": true,
		"is_voting": true,
		"last_vote_time": "2020-05-02T06:48:27",
		"lifetime_fees_paid": "124211826583",
		"pending_fees": 0,
		"pending_vested_fees": 0
	}
*/
    }

    @Ignore val owner: AccountObject
    @Ignore val name: String
//    @Ignore val mostRecentOperation: String
    @Ignore val totalOperations: Long
    @Ignore val removedOperations: Long
    @Ignore val coreInOrders: Long
    @Ignore val coreInBalance: Long
    @Ignore val hasCashback: Boolean
    @Ignore val isVoting: Boolean
    @Ignore val lastVoteTime: Date
    @Ignore val lifetimeFeesPaid: Long
    @Ignore val pendingFees: Long
    @Ignore val pendingVestingFees: Long

    init {
        owner = rawJson.optGrapheneInstance(KEY_OWNER)
        name = rawJson.optString(KEY_NAME)
        totalOperations = rawJson.optLong(KEY_TOTAL_OPS)
        removedOperations = rawJson.optLong(KEY_REMOVED_OPS)
        coreInOrders = rawJson.optLong(KEY_TOTAL_CORE_IN_ORDERS)
        coreInBalance = rawJson.optLong(KEY_CORE_IN_BALANCE)
        hasCashback = rawJson.optBoolean(KEY_HAS_CASHBACK_VB)
        isVoting = rawJson.optBoolean(KEY_IS_VOTING)
        lastVoteTime = rawJson.optGrapheneTime(KEY_LAST_VOTE_TIME)
        lifetimeFeesPaid = rawJson.optLong(KEY_LIFETIME_FEES_PAID)
        pendingFees = rawJson.optLong(KEY_PENDING_FEES)
        pendingVestingFees = rawJson.optLong(KEY_PENDING_VESTED_FEES)
    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}

