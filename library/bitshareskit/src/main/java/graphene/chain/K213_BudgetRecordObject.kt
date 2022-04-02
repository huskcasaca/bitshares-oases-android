package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class K213_BudgetRecordObject(
    @SerialName("id")
    override val id: K213_BudgetRecordIdType,
    @SerialName("time")
    val time: ChainTimePoint,
    @SerialName("record")
    val record: BudgetRecord,
) : AbstractObject(), K213_BudgetRecordType {
}

@Serializable
data class BudgetRecord(
    @SerialName("time_since_last_budget")
    val time_since_last_budget: uint64_t = 0U,
    // Sources of budget
    @SerialName("from_initial_reserve")
    val from_initial_reserve: share_type = 0U,
    @SerialName("from_accumulated_fees")
    val from_accumulated_fees: share_type = 0U,
    @SerialName("from_unused_witness_budget")
    val from_unused_witness_budget: share_type = 0U,
    // Witness budget requested by the committee
    @SerialName("requested_witness_budget")
    val requested_witness_budget: share_type = 0U,

    // Funds that can be released from reserve at maximum rate
    @SerialName("total_budget")
    val total_budget: share_type = 0U,

    // Sinks of budget, should sum up to total_budget
    @SerialName("witness_budget")
    val witness_budget: share_type = 0U,
    @SerialName("worker_budget")
    val worker_budget: share_type = 0U,
    // Unused budget
    @SerialName("leftover_worker_funds")
    val leftover_worker_funds: share_type = 0U,
    // Change in supply due to budget operations
    @SerialName("supply_delta")
    val supply_delta: share_type = 0U,
    // Maximum supply
    @SerialName("max_supply")
    val max_supply: share_type,
    // Current supply
    @SerialName("current_supply")
    val current_supply: share_type,
) {

}