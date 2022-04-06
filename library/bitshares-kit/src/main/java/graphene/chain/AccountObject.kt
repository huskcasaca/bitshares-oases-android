package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K102_AccountObject(
    @SerialName("id")
    override val id: AccountId,
    @SerialName("membership_expiration_date") @Serializable(with = TimePointSecSerializer::class)
    override val membershipExpirationDate: Instant,

    @SerialName("registrar")
    override val registrar: AccountIdType,
    @SerialName("referrer")
    override val referrer: AccountIdType,
    @SerialName("lifetime_referrer")
    override val lifetimeReferrer: AccountIdType,

    @SerialName("network_fee_percentage")
    override val networkFeePercentage: UInt16,
    @SerialName("lifetime_referrer_fee_percentage")
    override val lifetimeReferrerFeePercentage: UInt16,
    @SerialName("referrer_rewards_percentage")
    override val referrerRewardsFeePercentage: UInt16,

    @SerialName("name")
    override val name: String,

    @SerialName("owner")
    override val owner: Authority,
    @SerialName("active")
    override val active: Authority,
    @SerialName("options")
    override val options: AccountOptions,

    @SerialName("num_committee_voted")
    override val numCommitteeVoted: UInt16,
    @SerialName("statistics")
    override val statistics: AccountStatisticsIdType,

    @SerialName("whitelisting_accounts")
    override val whiteListingAccounts: TypeSet<AccountIdType>,
    @SerialName("blacklisting_accounts")
    override val blackListingAccounts: TypeSet<AccountIdType>,
    @SerialName("whitelisted_accounts")
    override val whiteListedAccounts: Set<AccountIdType>,
    @SerialName("blacklisted_accounts")
    override val blackListedAccounts: Set<AccountIdType>,

    @SerialName("cashback_vb")
    override val cashbackVestingBalance: Optional<VestingBalanceId> = optional(),

    @SerialName("owner_special_authority")
    override val ownerSpecialAuthority: SpecialAuthority,
    @SerialName("active_special_authority")
    override val activeSpecialAuthority: SpecialAuthority,
    @SerialName("top_n_control_flags")
    override val topNControlFlags: UInt8,

    @SerialName("allowed_assets")
    override val allowedAssets: Optional<FlatSet<AccountIdType>> = optional(),
) : AbstractObject(), AccountIdType {
    companion object {
        const val TOP_N_CONTROL_OWNER  : UInt8 = 0x01U
        const val TOP_N_CONTROL_ACTIVE : UInt8 = 0x02U
    }
}

@Serializable
data class K205_AccountBalanceObject(
    @SerialName("id")
    override val id: AccountBalanceId,
    @SerialName("owner")
    override val owner: AccountIdType,
    @SerialName("asset_type")
    override val asset: AssetIdType,
    @SerialName("balance")
    override val balance: ShareType,
    @SerialName("maintenance_flag")
    override val maintenanceFlag: Boolean = false,  // Whether need to process this balance object in maintenance interval
) : AbstractObject(), AccountBalanceIdType {
//
//    asset get_balance()const { return asset(balance, asset_type); }
//    void  adjust_balance(const asset& delta);
}

@Serializable
data class K206_AccountStatisticsObject(
    @SerialName("id")
    override val id: AccountStatisticsId,
    @SerialName("owner")
    val owner: AccountIdType,
    @SerialName("name") //< redundantly store account name here for better maintenance performance
    val name: String,
    /**
     * Keep the most recent operation as a root pointer to a linked list of the transaction history.
     */
    @SerialName("most_recent_op")
    val mostRecentOp: AccountTransactionHistoryIdType,
    /** Total operations related to this account. */
    @SerialName("total_ops")
    val totalOps: UInt64,
    /** Total operations related to this account that has been removed from the database. */
    @SerialName("removed_ops")
    val removedOps: UInt64,
    /**
     * When calculating votes it is necessary to know how much is stored in orders (and thus unavailable for
     * transfers). Rather than maintaining an index of [asset,owner,order_id] we will simply maintain the running
     * total here and update it every time an order is created or modified.
     */
    @SerialName("total_core_in_orders")
    val totalCoreInOrders: ShareType,
    // Total amount of core token in inactive lock_forever tickets
    @SerialName("total_core_inactive")
    val totalCoreInactive: ShareType,
    // Total amount of core token in active lock_forever tickets
    @SerialName("total_core_pob")
    val totalCorePob: ShareType,
    // Total amount of core token in other tickets
    @SerialName("total_core_pol")
    val totalCorePol: ShareType,
    // Total value of tickets whose current type is lock_forever
    @SerialName("total_pob_value")
    val totalPobValue: ShareType,
    // Total value of tickets whose current type is not lock_forever
    @SerialName("total_pol_value")
    val totalPolValue: ShareType,
    // Redundantly store core balance in this object for better maintenance performance.
    // Only updates on maintenance.
    @SerialName("core_in_balance")
    val coreInBalance: ShareType,
    // redundantly store this for better maintenance performance
    @SerialName("has_cashback_vb")
    val hasCashbackVestingBalance: Boolean = false,
    // redundately store "if this account is voting" for better maintenance performance
    @SerialName("is_voting")
    val isVoting: Boolean = false,
    // last time voted
    @SerialName("last_vote_time") @Serializable(TimePointSecSerializer::class)
    val lastVoteTime: Instant,
    // Voting Power Stats
    @SerialName("vp_all")
    val allVotingPower: UInt64 = 0U,            // all voting power.
    @SerialName("vp_active")
    val activeVotingPower: UInt64 = 0U,         // active voting power, if there is no attenuation, it is equal to vp_all.
    @SerialName("vp_committee")
    val committeeVotingPower: UInt64 = 0U,      // the final voting power for the committees.
    @SerialName("vp_witness")
    val witnessVotingPower: UInt64 = 0U,        // the final voting power for the witnesses.
    @SerialName("vp_worker")
    val workerVotingPower: UInt64 = 0U,         // the final voting power for the workers.
    // timestamp of the last count of vot.
    // if there is no statistics, the date is less than `_db.get_dynamic_global_properties().last_vote_tally_time`.
    @SerialName("vote_tally_time") @Serializable(TimePointSecSerializer::class)
    val voteTallyTime: Instant,
    /**
     * Tracks the total fees paid by this account for the purpose of calculating bulk discounts.
     */
    @SerialName("lifetime_fees_paid")
    val lifetimeFeesPaid: ShareType,
    /**
     * Tracks the fees paid by this account which have not been disseminated to the various parties that receive
     * them yet (registrar, referrer, lifetime referrer, network, etc). This is used as an optimization to avoid
     * doing massive amounts of uint128 arithmetic on each and every operation.
     *
     * These fees will be paid out as vesting cash-back, and this counter will reset during the maintenance
     * interval.
     */
    @SerialName("pending_fees")
    val pendingFees: ShareType,
//    /**
//     * Same as @ref pending_fees, except these fees will be paid out as pre-vested cash-back (immediately
//     * available for withdrawal) rather than requiring the normal vesting period.
//     */
    @SerialName("pending_vested_fees")
    val pendingVestedFees: ShareType,
    ) : AbstractObject(), AccountStatisticsIdType {

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
