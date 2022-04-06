package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class K213_BudgetRecordObject(
    @SerialName("id")
    override val id: BudgetRecordId,
    @SerialName("time") @Serializable(TimePointSecSerializer::class)
    val time: Instant,
    @SerialName("record")
    val record: BudgetRecord,
) : AbstractObject(), BudgetRecordIdType {
}

@Serializable
data class BudgetRecord(
    @SerialName("time_since_last_budget")
    val timeSinceLastBudget: UInt64, // = 0U,
    // Sources of budget
    @SerialName("from_initial_reserve")
    val fromInitialReserve: ShareType, // = 0U,
    @SerialName("from_accumulated_fees")
    val fromAccumulatedFees: ShareType, // = 0U,
    @SerialName("from_unused_witness_budget")
    val fromUnusedWitnessBudget: ShareType, // = 0U,
    // Witness budget requested by the committee
    @SerialName("requested_witness_budget")
    val requestedWitnessBudget: ShareType, // = 0U,

    // Funds that can be released from reserve at maximum rate
    @SerialName("total_budget")
    val totalBudget: ShareType, // = 0U,

    // Sinks of budget, should sum up to total_budget
    @SerialName("witness_budget")
    val witnessBudget: ShareType, // = 0U,
    @SerialName("worker_budget")
    val workerBudget: ShareType, // = 0U,
    // Unused budget
    @SerialName("leftover_worker_funds")
    val leftoverWorkerFunds: ShareType, // = 0U,
    // Change in supply due to budget operations
    @SerialName("supply_delta")
    val supplyDelta: ShareType, // = 0U,
    // Maximum supply
    @SerialName("max_supply")
    val maxSupply: ShareType,
    // Current supply
    @SerialName("current_supply")
    val currentSupply: ShareType,
) {

}