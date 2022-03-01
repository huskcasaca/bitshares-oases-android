package bitshareskit.models

import androidx.room.ColumnInfo
import bitshareskit.extensions.optIterable
import bitshareskit.objects.*
import bitshareskit.objects.GrapheneObject.Companion.fromJson
import bitshareskit.objects.GrapheneObject.Companion.fromJsonOnly
import org.java_json.JSONObject

data class FullAccount(@ColumnInfo(name = "data") val rawJson: JSONObject) {

    companion object {
        const val KEY_ACCOUNT = "account"
        const val KEY_STATISTICS = "statistics"
        const val KEY_REGISTRAR_NAME = "registrar_name"
        const val KEY_REFERRER_NAME = "referrer_name"
        const val KEY_LIFETIME_REFERRER_NAME = "lifetime_referrer_name"
        const val KEY_VOTES = "votes"
        const val KEY_CASHBACK_BALANCE = "cashback_balance"
        const val KEY_BALANCE = "balances"
        const val KEY_VESTING_BALANCE = "vesting_balances"
        const val KEY_LIMIT_ORDERS = "limit_orders"
        const val KEY_CALL_ORDERS = "call_orders"
        const val KEY_SETTLE_ORDERS = "settle_orders"
        const val KEY_PROPOSALS = "proposals"
        const val KEY_ASSETS = "assets"
        const val KEY_WITHDRAWS_FROM = "withdraws_from"
        const val KEY_WITHDRAWS_TO = "withdraws_to"
        const val KEY_HTLCS_FROM = "htlcs_from"
        const val KEY_HTLCS_TO = "htlcs_to"
        const val KEY_MORE_DATA_AVAILABLE = "more_data_available"
    }

    val account: AccountObject by lazy { fromJson(rawJson.optJSONObject(KEY_ACCOUNT)) }
    val accountStatistics: AccountStatisticsObject by lazy { fromJson(rawJson.optJSONObject(KEY_STATISTICS)) }
    val balance: List<AccountBalanceObject> by lazy { rawJson.optIterable<JSONObject>(KEY_BALANCE).map { AccountBalanceObject(it) } }
    val votes: List<GrapheneObject> by lazy { rawJson.optIterable<JSONObject>(KEY_VOTES).map { fromJsonOnly(it) } }
    val witnessVotes: List<WitnessObject> by lazy { votes.filterIsInstance<WitnessObject>() }
    val workerVotes: List<WorkerObject> by lazy { votes.filterIsInstance<WorkerObject>() }
    val committeeVotes: List<CommitteeMemberObject> by lazy { votes.filterIsInstance<CommitteeMemberObject>() }
    val limitOrders: List<LimitOrderObject> by lazy { rawJson.optIterable<JSONObject>(KEY_LIMIT_ORDERS).map { LimitOrderObject(it) } }
    val callOrders: List<CallOrderObject> by lazy { rawJson.optIterable<JSONObject>(KEY_CALL_ORDERS).map { CallOrderObject(it) } }


}
