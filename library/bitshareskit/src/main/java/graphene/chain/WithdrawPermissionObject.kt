package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K112_WithdrawPermissionObject(
    @SerialName("id")
    override val id: WithdrawPermissionId,
    // The account authorizing @ref authorized_account to withdraw from it
    @SerialName("withdraw_from_account")
    val withdrawFromAccount: AccountIdType,
    // The account authorized to make withdrawals from @ref withdraw_from_account
    @SerialName("authorized_account")
    val authorizedAccount: AccountIdType,
    // The maximum amount which may be withdrawn per period. All withdrawals must be of this asset type
    @SerialName("withdrawal_limit")
    val withdrawalLimit: Asset,
    // The duration of a withdrawal period in seconds
    @SerialName("withdrawal_period_sec")
    val withdrawalPeriodSec: UInt32 = 0U,
    /***
     * The beginning of the next withdrawal period
     * WARNING: Due to caching, this value does not always represent the start of the next or current period (because it is only updated after a withdrawal operation such as claim).  For the latest current period, use current_period().
     */
    @SerialName("period_start_time") @Serializable(TimePointSecSerializer::class)
    val periodStartTime: Instant,
    // The time at which this withdraw permission expires
    @SerialName("expiration") @Serializable(TimePointSecSerializer::class)
    val expiration: Instant,
    /***
     * Tracks the total amount
     * WARNING: Due to caching, this value does not always represent the total amount claimed during the current period; it may represent what was claimed during the last claimed period (because it is only updated after a withdrawal operation such as claim).  For the latest current period, use current_period().
     */
    @SerialName("claimed_this_period")
    val claimedThisPeriod: ShareType,
) : AbstractObject(), WithdrawPermissionIdType {


//    /***
//     * Determine how much is still available to be claimed during the period that contains a time of interest.  This object and function is mainly intended to be used with the "current" time as a parameter.  The current time can be obtained from the time of the current head of the blockchain.
//     */
//    asset              available_this_period( fc::time_point_sec current_time )const
//    {
//        if( current_time >= period_start_time + withdrawal_period_sec )
//            return withdrawal_limit;
//        return asset(
//            ( withdrawal_limit.amount > claimed_this_period )
//            ? withdrawal_limit.amount - claimed_this_period
//        : 0, withdrawal_limit.asset_id );
//    }
}
