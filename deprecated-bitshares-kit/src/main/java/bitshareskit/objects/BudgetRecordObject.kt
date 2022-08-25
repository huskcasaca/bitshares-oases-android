package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.extensions.optGrapheneTime
import org.java_json.JSONObject
import java.util.*

@Entity(tableName = BudgetRecordObject.TABLE_NAME)
data class BudgetRecordObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    /*{
        "id":"2.13.41099",
        "time":"2020-06-21T11:00:00",
        "record":{
            "time_since_last_budget":3600,
            "from_initial_reserve":"85018113328357",
            "from_accumulated_fees":2115629,
            "from_unused_witness_budget":300000,
            "requested_witness_budget":120000000,
            "total_budget":1211443144,
            "witness_budget":120000000,
            "worker_budget":1091443144,
            "leftover_worker_funds":0,
            "supply_delta":1209027515
        }
    }*/

    companion object {
        @Ignore const val TABLE_NAME = "budget_record_object"
        @Ignore const val KEY_TIME = "time"
        @Ignore const val KEY_RECORD = "record"
        @Ignore const val KEY_WITNESS_BUDGET = "witness_budget"
        @Ignore const val KEY_WORKER_BUDGET = "worker_budget"


    }

    @Ignore val time: Date
    @Ignore val witnessBudget: Long
    @Ignore val workerBudget: Long


    init {
        time = rawJson.optGrapheneTime(KEY_TIME)
        val record = rawJson.optJSONObject(KEY_RECORD)
        witnessBudget = record.optLong(KEY_WITNESS_BUDGET)
        workerBudget = record.optLong(KEY_WORKER_BUDGET)

    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
