package graphene.chain

import graphene.protocol.*
import graphene.serializers.StaticVarSerializer
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class K114_WorkerObject(
    @SerialName("id")
    override val id: WorkerId,
    // ID of the account which owns this worker
    @SerialName("worker_account")
    val workerAccount: AccountIdType,
    // Time at which this worker begins receiving pay, if elected
    @SerialName("work_begin_date") @Serializable(TimePointSecSerializer::class)
    val workBeginDate: Instant,
    // Time at which this worker will cease to receive pay. Worker will be deleted at this time
    @SerialName("work_end_date") @Serializable(TimePointSecSerializer::class)
    val workEndDate: Instant,
    // Amount in CORE this worker will be paid each day
    @SerialName("daily_pay")
    val dailyPay: ShareType,
    // ID of this worker's pay balance
    @SerialName("worker")
    val worker: WorkerType,
    // Human-readable name for the worker
    @SerialName("name")
    val name: String,
    // URL to a web page representing this worker
    @SerialName("url")
    val url: String,
    // Voting ID which represents approval of this worker
    @SerialName("vote_for")
    val voteFor: VoteIdType,
    // Voting ID which represents disapproval of this worker
    @SerialName("vote_against")
    val voteAgainst: VoteIdType,
    @SerialName("total_votes_for")
    val totalVotesFor: UInt64 = 0U,
    @SerialName("total_votes_against")
    val totalVotesAgainst: UInt64 = 0U,
) : AbstractObject(), WorkerIdType {
//    bool is_active(fc::time_point_sec now)const {
//        return now >= work_begin_date && now <= work_end_date;
//    }
//    share_type approving_stake()const {
//        return int64_t( total_votes_for ) - int64_t( total_votes_against );
//    }
}

@Serializable(with = WorkerTypeSerializer::class)
sealed class WorkerType

/**
 * @brief A worker who returns all of his pay to the reserve
 *
 * This worker type pays everything he receives back to the network's reserve funds pool.
 */
@Serializable
data class RefundWorkerType(
    // Record of how much this worker has burned in his lifetime
    @SerialName("total_burned") val totalBurned: ShareType,
) : WorkerType() {
//    void pay_worker(share_type pay, database&);
}

/**
 * @brief A worker who sends his pay to a vesting balance
 *
 * This worker type takes all of his pay and places it into a vesting balance
 */
@Serializable
data class VestingBalanceWorkerType(
    // The balance this worker pays into
    @SerialName("balance") val balance: VestingBalanceIdType,
): WorkerType() {
//    void pay_worker(share_type pay, database& db);
}

/**
 * @brief A worker who permanently destroys all of his pay
 *
 * This worker sends all pay he receives to the null account.
 */
@Serializable
data class BurnWorkerType(
    // Record of how much this worker has burned in his lifetime
    @SerialName("total_burned") val totalBurned: ShareType,
): WorkerType() {
//    void pay_worker(share_type pay, database&);
}

object WorkerTypeSerializer : StaticVarSerializer<WorkerType>(
    listOf(
        RefundWorkerType::class,
        VestingBalanceWorkerType::class,
        BurnWorkerType::class,
    )
)
