package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optGrapheneTime
import org.java_json.JSONObject
import java.util.*

@Entity(tableName = DynamicGlobalPropertyObject.TABLE_NAME)
class DynamicGlobalPropertyObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    /*{
        "id": "2.1.0",
        "head_block_number": 47983420,
        "head_block_id": "02dc2b3cbd5bb0e3565523d3ef9339d6058ffbc7",
        "time": "2020-05-16T14:01:09",
        "current_witness": "1.6.157",
        "next_maintenance_time": "2020-05-16T15:00:00",
        "last_budget_time": "2020-05-16T14:00:00",
        "witness_budget": 118000000,
        "accounts_registered_this_interval": 0,
        "recently_missed_count": 0,
        "current_aslot": 48178106,
        "recent_slots_filled": "340282366920938463463374607431768211455",
        "dynamic_flags": 0,
        "last_irreversible_block_num": 47983406
    }*/

    companion object {
        @Ignore const val TABLE_NAME = "dynamic_global_property_object"

        @Ignore const val KEY_HEAD_BLOCK_NUMBER = "head_block_number"
        @Ignore const val KEY_HEAD_BLOCK_ID = "head_block_id"
        @Ignore const val KEY_TIME = "time"
        @Ignore const val KEY_CURRENT_WITNESS = "current_witness"
        @Ignore const val KEY_NEXT_MAINTENANCE_TIME = "next_maintenance_time"
        @Ignore const val KEY_LAST_BUDGET_TIME = "last_budget_time"
        @Ignore const val KEY_WITNESS_BUDGET = "witness_budget"
        @Ignore const val KEY_ACCOUNTS_REGISTERED_THIS_INTERVAL = "accounts_registered_this_interval"
        @Ignore const val KEY_RECENTLY_MISSED_COUNT = "recently_missed_count"
        @Ignore const val KEY_CURRENT_ASLOT = "current_aslot"
        @Ignore const val KEY_RECENT_SLOTS_FILLED = "recent_slots_filled"
        @Ignore const val KEY_DYNAMIC_FLAGS = "dynamic_flags"
        @Ignore const val KEY_LAST_IRREVERSIBLE_BLOCK_NUM = "last_irreversible_block_num"
    }

    @Ignore val headBlockNumber: Long
    @Ignore val headBlockId: String
    @Ignore val time: Date
    @Ignore val currentWitness: WitnessObject
    @Ignore val nextMaintenanceTime: Date
    @Ignore val lastBudgetTime: Date
    @Ignore val witnessBudget: Long
    @Ignore val accountsRegisteredThisInterval: Long
    @Ignore val recentlyMissedCount: Int
    @Ignore val currentAslot: Int
    @Ignore val recentSlotsFilled: String
    @Ignore val dynamicFlags: Int
    @Ignore val lastIrreversibleBlockNum: Long


    init {
        headBlockNumber = rawJson.optLong(KEY_HEAD_BLOCK_NUMBER)
        headBlockId = rawJson.optString(KEY_HEAD_BLOCK_ID)
        time = rawJson.optGrapheneTime(KEY_TIME)
        currentWitness = rawJson.optGrapheneInstance(KEY_CURRENT_WITNESS)
        lastBudgetTime = rawJson.optGrapheneTime(KEY_LAST_BUDGET_TIME)
        nextMaintenanceTime = rawJson.optGrapheneTime(KEY_NEXT_MAINTENANCE_TIME)
        witnessBudget = rawJson.optLong(KEY_WITNESS_BUDGET)
        accountsRegisteredThisInterval = rawJson.optLong(KEY_ACCOUNTS_REGISTERED_THIS_INTERVAL)
        recentlyMissedCount = rawJson.optInt(KEY_RECENTLY_MISSED_COUNT)
        currentAslot = rawJson.optInt(KEY_CURRENT_ASLOT)
        recentSlotsFilled = rawJson.optString(KEY_RECENT_SLOTS_FILLED)
        dynamicFlags = rawJson.optInt(KEY_DYNAMIC_FLAGS)
        lastIrreversibleBlockNum = rawJson.optLong(KEY_LAST_IRREVERSIBLE_BLOCK_NUM)
    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
