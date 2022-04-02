package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K206_AccountStatisticsObject(
    @SerialName("id")
    override val id: AccountStatisticsIdType,
    @SerialName("owner")
    val owner: AccountType,
    @SerialName("name") //< redundantly store account name here for better maintenance performance
    val name: String,
    /**
     * Keep the most recent operation as a root pointer to a linked list of the transaction history.
     */
    @SerialName("most_recent_op")
    val most_recent_op: AccountTransactionHistoryType,
    /** Total operations related to this account. */
    @SerialName("total_ops")
    val total_ops: uint64_t,
    /** Total operations related to this account that has been removed from the database. */
    @SerialName("removed_ops")
    val removed_ops: uint64_t,
    /**
     * When calculating votes it is necessary to know how much is stored in orders (and thus unavailable for
     * transfers). Rather than maintaining an index of [asset,owner,order_id] we will simply maintain the running
     * total here and update it every time an order is created or modified.
     */
    @SerialName("total_core_in_orders")
    val total_core_in_orders: share_type,
    // Total amount of core token in inactive lock_forever tickets
    @SerialName("total_core_inactive")
    val total_core_inactive: share_type,
    // Total amount of core token in active lock_forever tickets
    @SerialName("total_core_pob")
    val total_core_pob: share_type,
    // Total amount of core token in other tickets
    @SerialName("total_core_pol")
    val total_core_pol: share_type,
    // Total value of tickets whose current type is lock_forever
    @SerialName("total_pob_value")
    val total_pob_value: share_type,
    // Total value of tickets whose current type is not lock_forever
    @SerialName("total_pol_value")
    val total_pol_value: share_type,
    // Redundantly store core balance in this object for better maintenance performance.
    // Only updates on maintenance.
    @SerialName("core_in_balance")
    val core_in_balance: share_type,
    // redundantly store this for better maintenance performance
    @SerialName("has_cashback_vb")
    val has_cashback_vb: Boolean = false,
    // redundately store "if this account is voting" for better maintenance performance
    @SerialName("is_voting")
    val is_voting: Boolean = false,
    // last time voted
    @SerialName("last_vote_time")
    val last_vote_time: ChainTimePoint,
    // Voting Power Stats
    @SerialName("vp_all")
    val vpAll: uint64_t = 0U,            // all voting power.
    @SerialName("vp_active")
    val vpActive: uint64_t = 0U,         // active voting power, if there is no attenuation, it is equal to vp_all.
    @SerialName("vp_committee")
    val vpCommittee: uint64_t = 0U,      // the final voting power for the committees.
    @SerialName("vp_witness")
    val vpWitness: uint64_t = 0U,        // the final voting power for the witnesses.
    @SerialName("vp_worker")
    val vpWorker: uint64_t = 0U,         // the final voting power for the workers.
    // timestamp of the last count of vot.
    // if there is no statistics, the date is less than `_db.get_dynamic_global_properties().last_vote_tally_time`.
    @SerialName("vote_tally_time")
    val voteTallyTime: ChainTimePoint,
    /**
     * Tracks the total fees paid by this account for the purpose of calculating bulk discounts.
     */
    @SerialName("lifetime_fees_paid")
    val lifetimeFeesPaid: share_type,
    /**
     * Tracks the fees paid by this account which have not been disseminated to the various parties that receive
     * them yet (registrar, referrer, lifetime referrer, network, etc). This is used as an optimization to avoid
     * doing massive amounts of uint128 arithmetic on each and every operation.
     *
     * These fees will be paid out as vesting cash-back, and this counter will reset during the maintenance
     * interval.
     */
    @SerialName("pending_fees")
    val pendingFees: share_type,
//    /**
//     * Same as @ref pending_fees, except these fees will be paid out as pre-vested cash-back (immediately
//     * available for withdrawal) rather than requiring the normal vesting period.
//     */
    @SerialName("pending_vested_fees")
    val pendingVestedFees: share_type,

    ) : AbstractObject(), AccountStatisticsType {

//
//    /// Whether this account owns some CORE asset and is voting
//    inline bool has_some_core_voting() const
//            {
//                return is_voting && ( total_core_in_orders > 0 || core_in_balance > 0 || has_cashback_vb
//                        || total_core_pol > 0 );
//            }
//
//    /// Whether this account has pending fees, no matter vested or not
//    inline bool has_pending_fees() const { return pending_fees > 0 || pending_vested_fees > 0; }
//
//    /// Whether need to process this account during the maintenance interval
//    inline bool need_maintenance() const { return has_some_core_voting() || has_pending_fees(); }
//
//    /// @brief Split up and pay out @ref pending_fees and @ref pending_vested_fees
//    void process_fees(const account_object& a, database& d) const;
//
//    /**
//     * Core fees are paid into the account_statistics_object by this method
//     */
//    void pay_fee( share_type core_fee, share_type cashback_vesting_threshold );

}