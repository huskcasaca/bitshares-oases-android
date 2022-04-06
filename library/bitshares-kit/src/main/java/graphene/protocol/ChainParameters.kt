package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//struct fee_schedule;
//

@Serializable
data class HtlcOptions(
    @SerialName("max_timeout_secs")  val maxTimeoutSecs : UInt32,
    @SerialName("max_preimage_size") val maxPreimageSize: UInt32,
)

@Serializable
data class CustomAuthorityOptionsType(
    @SerialName("max_custom_authority_lifetime_seconds") val maxCustomAuthorityLifetimeSeconds  : UInt32 = GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITY_LIFETIME_SECONDS,
    @SerialName("max_custom_authorities_per_account")    val maxCustomAuthoritiesPerAccount     : UInt32 = GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITIES_PER_ACCOUNT,
    @SerialName("max_custom_authorities_per_account_op") val maxCustomAuthoritiesPerAccountOp   : UInt32 = GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITIES_PER_ACCOUNT_OP,
    @SerialName("max_custom_authority_restrictions")     val maxCustomAuthorityRestrictions     : UInt32 = GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITY_RESTRICTIONS,
)

@Serializable
data class ChainParameters(
    @SerialName("current_fees")                        val currentFees                      : FeeSchedule, // std::shared_ptr<const fee_schedule>       /** using a shared_ptr breaks the circular dependency created between operations and the fee schedule */                 //< current schedule of fees

    @SerialName("block_interval")                      val blockInterval                    : UInt8     = GRAPHENE_DEFAULT_BLOCK_INTERVAL, //< interval in seconds between blocks
    @SerialName("maintenance_interval")                val maintenanceInterval              : UInt32    = GRAPHENE_DEFAULT_MAINTENANCE_INTERVAL, //< interval in sections between blockchain maintenance events
    @SerialName("maintenance_skip_slots")              val maintenanceSkipSlots             : UInt8     = GRAPHENE_DEFAULT_MAINTENANCE_SKIP_SLOTS, //< number of block_intervals to skip at maintenance time
    @SerialName("committee_proposal_review_period")    val committeeProposalReviewPeriod    : UInt32    = GRAPHENE_DEFAULT_COMMITTEE_PROPOSAL_REVIEW_PERIOD_SEC, //< minimum time in seconds that a proposed transaction requiring committee authority may not be signed, prior to expiration
    @SerialName("maximum_transaction_size")            val maximumTransactionSize           : UInt32    = GRAPHENE_DEFAULT_MAX_TRANSACTION_SIZE, //< maximum allowable size in bytes for a transaction
    @SerialName("maximum_block_size")                  val maximumBlockSize                 : UInt32    = GRAPHENE_DEFAULT_MAX_BLOCK_SIZE, //< maximum allowable size in bytes for a block
    @SerialName("maximum_time_until_expiration")       val maximumTimeUntilExpiration       : UInt32    = GRAPHENE_DEFAULT_MAX_TIME_UNTIL_EXPIRATION, //< maximum lifetime in seconds for transactions to be valid, before expiring
    @SerialName("maximum_proposal_lifetime")           val maximumProposalLifetime          : UInt32    = GRAPHENE_DEFAULT_MAX_PROPOSAL_LIFETIME_SEC, //< maximum lifetime in seconds for proposed transactions to be kept, before expiring
    @SerialName("maximum_asset_whitelist_authorities") val maximumAssetWhitelistAuthorities : UInt8     = GRAPHENE_DEFAULT_MAX_ASSET_WHITELIST_AUTHORITIES, //< maximum number of accounts which an asset may list as authorities for its whitelist OR blacklist
    @SerialName("maximum_asset_feed_publishers")       val maximumAssetFeedPublishers       : UInt8     = GRAPHENE_DEFAULT_MAX_ASSET_FEED_PUBLISHERS, //< the maximum number of feed publishers for a given asset
    @SerialName("maximum_witness_count")               val maximumWitnessCount              : UInt16    = GRAPHENE_DEFAULT_MAX_WITNESSES, //< maximum number of active witnesses
    @SerialName("maximum_committee_count")             val maximumCommitteeCount            : UInt16    = GRAPHENE_DEFAULT_MAX_COMMITTEE, //< maximum number of active committee_members
    @SerialName("maximum_authority_membership")        val maximumAuthorityMembership       : UInt16    = GRAPHENE_DEFAULT_MAX_AUTHORITY_MEMBERSHIP, //< largest number of keys/accounts an authority can have
    @SerialName("reserve_percent_of_fee")              val reservePercentOfFee              : UInt16    = GRAPHENE_DEFAULT_BURN_PERCENT_OF_FEE, //< the percentage of the network's allocation of a fee that is taken out of circulation
    @SerialName("network_percent_of_fee")              val networkPercentOfFee              : UInt16    = GRAPHENE_DEFAULT_NETWORK_PERCENT_OF_FEE, //< percent of transaction fees paid to network
    @SerialName("lifetime_referrer_percent_of_fee")    val lifetimeReferrerPercentOfFee     : UInt16    = GRAPHENE_DEFAULT_LIFETIME_REFERRER_PERCENT_OF_FEE, //< percent of fee which should go to lifetime referrer
    @SerialName("cashback_vesting_period_seconds")     val cashbackVestingPeriodSeconds     : UInt32    = GRAPHENE_DEFAULT_CASHBACK_VESTING_PERIOD_SEC, //< time after cashback rewards are accrued before they become liquid
    @SerialName("cashback_vesting_threshold")          val cashbackVestingThreshold         : ShareType = GRAPHENE_DEFAULT_CASHBACK_VESTING_THRESHOLD, //< the maximum cashback that can be received without vesting
    @SerialName("count_non_member_votes")              val countNonMemberVotes              : Boolean   = true, //< set to false to restrict voting privlegages to member accounts
    @SerialName("allow_non_member_whitelists")         val allowNonMemberWhitelists         : Boolean   = false, //< true if non-member accounts may set whitelists and blacklists; false otherwise
    @SerialName("witness_pay_per_block")               val witnessPayPerBlock               : ShareType = GRAPHENE_DEFAULT_WITNESS_PAY_PER_BLOCK, //< CORE to be allocated to witnesses (per block)
    @SerialName("witness_pay_vesting_seconds")         val witnessPayVestingSeconds         : UInt32    = GRAPHENE_DEFAULT_WITNESS_PAY_VESTING_SECONDS, //< vesting_seconds parameter for witness VBO's
    @SerialName("worker_budget_per_day")               val workerBudgetPerDay               : ShareType = GRAPHENE_DEFAULT_WORKER_BUDGET_PER_DAY, //< CORE to be allocated to workers (per day)
    @SerialName("max_predicate_opcode")                val maxPredicateOpcode               : UInt16    = GRAPHENE_DEFAULT_MAX_ASSERT_OPCODE, //< predicate_opcode must be less than this number
    @SerialName("fee_liquidation_threshold")           val feeLiquidationThreshold          : ShareType = GRAPHENE_DEFAULT_FEE_LIQUIDATION_THRESHOLD, //< value in CORE at which accumulated fees in blockchain-issued market assets should be liquidated
    @SerialName("accounts_per_fee_scale")              val accountsPerFeeScale              : UInt16    = GRAPHENE_DEFAULT_ACCOUNTS_PER_FEE_SCALE, //< number of accounts between fee scalings
    @SerialName("account_fee_scale_bitshifts")         val accountFeeScaleBitshifts         : UInt8     = GRAPHENE_DEFAULT_ACCOUNT_FEE_SCALE_BITSHIFTS, //< number of times to left bitshift account registration fee at each scaling
    @SerialName("max_authority_depth")                 val maxAuthorityDepth                : UInt8     = GRAPHENE_MAX_SIG_CHECK_DEPTH,
    @SerialName("extensions")                          val extensions                       : Optional<ChainParametersExtension> = optional(),
) {

    @Serializable
    data class ChainParametersExtension(
        @SerialName("updatable_htlc_options")      val updatableHtlcOptions    : Optional<HtlcOptions> = optional(),
        @SerialName("custom_authority_options")    val customAuthorityOptions  : Optional<CustomAuthorityOptionsType> = optional(),
        @SerialName("market_fee_network_percent")  val marketFeeNetworkPercent : Optional<UInt16> = optional(),
        @SerialName("maker_fee_discount_percent")  val makerFeeDiscountPercent : Optional<UInt16> = optional(),
    ): Extension<ChainParametersExtension>

//    const fee_schedule& get_current_fees() const { FC_ASSERT(current_fees); return *current_fees; }
//    fee_schedule& get_mutable_fees() { FC_ASSERT(current_fees); return const_cast<fee_schedule&>(*current_fees); }

//    void validate()const;
//
//    chain_parameters();
//    chain_parameters(const chain_parameters& other);
//    chain_parameters(chain_parameters&& other);
//    chain_parameters& operator=(const chain_parameters& other);
//    chain_parameters& operator=(chain_parameters&& other);
//
//    // If @c market_fee_network_percent in @ref extensions is valid, return the value it contains,
//    // otherwise return 0
//    uint16_t get_market_fee_network_percent() const;
//
//    // If @c maker_fee_discount_percent in @ref extensions is valid, return the value it contains,
//    // otherwise return 0
//    uint16_t get_maker_fee_discount_percent() const;
//
//    private:
//    static void safe_copy(chain_parameters& to, const chain_parameters& from);

}