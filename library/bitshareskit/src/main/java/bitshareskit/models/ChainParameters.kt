package bitshareskit.models

import bitshareskit.extensions.*
import bitshareskit.objects.GrapheneSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

@Suppress("EXPERIMENTAL_API_USAGE")
data class ChainParameters(
    val currentFee: FeeSchedule,
    val blockInterval: UByte,
    val maintenanceInterval: UInt,
    val maintenanceSkipSlots: UByte,
    val committeeProposalReviewPeriod: UInt,
    val maximumTransactionSize: UInt,
    val maximumBlockSize: UInt,
    val maximumTimeUntilExpiration: UInt,
    val maximumProposalLifetime: UInt,
    val maximumAssetWhitelistAuthorities: UByte,
    val maximumAssetFeedPublishers: UByte,
    val maximumWitnessCount: UShort,
    val maximumCommitteeCount: UShort,
    val maximumAuthorityMembership: UShort,
    val reservePercentOfFee: UShort,
    val networkPercentOfFee: UShort,
    val lifetimeReferrerPercentOfFee: UShort,
    val cashbackVestingPeriodSeconds: UInt,
    val cashbackVestingThreshold: Long,
    val countNonMemberVotes: Boolean,
    val allowNonMemberWhitelists: Boolean,
    val witnessPayPerBlock: Long,
    val workerBudgetPerDay: Long,
    val maxPredicateOpcode: UShort,
    val feeLiquidationThreshold: Long,
    val accountsPerFeeScale: UShort,
    val accountFeeScaleBitshifts: UByte,
    val maxAuthorityDepth: UByte
): GrapheneSerializable {

    companion object {

        const val KEY_CURRENT_FEE = "current_fee"
        const val KEY_BLOCK_INTERVAL = "block_interval"
        const val KEY_MAINTENANCE_INTERVAL = "maintenance_interval"
        const val KEY_MAINTENANCE_SKIP_SLOTS = "maintenance_skip_slots"
        const val KEY_COMMITTEE_PROPOSAL_REVIEW_PERIOD = "committee_proposal_review_period"
        const val KEY_MAXIMUM_TRANSACTION_SIZE = "maximum_transaction_size"
        const val KEY_MAXIMUM_BLOCK_SIZE = "maximum_block_size"
        const val KEY_MAXIMUM_TIME_UNTIL_EXPIRATION = "maximum_time_until_expiration"
        const val KEY_MAXIMUM_PROPOSAL_LIFETIME = "maximum_proposal_lifetime"
        const val KEY_MAXIMUM_ASSET_WHITELIST_AUTHORITIES = "maximum_asset_whitelist_authorit"
        const val KEY_MAXIMUM_ASSET_FEED_PUBLISHERS = "maximum_asset_feed_publishers"
        const val KEY_MAXIMUM_WITNESS_COUNT = "maximum_witness_count"
        const val KEY_MAXIMUM_COMMITTEE_COUNT = "maximum_committee_count"
        const val KEY_MAXIMUM_AUTHORITY_MEMBERSHIP = "maximum_authority_membership"
        const val KEY_RESERVE_PERCENT_OF_FEE = "reserve_percent_of_fee"
        const val KEY_NETWORK_PERCENT_OF_FEE = "network_percent_of_fee"
        const val KEY_LIFETIME_REFERRER_PERCENT_OF_FEE = "lifetime_referrer_percent_of_fee"
        const val KEY_CASHBACK_VESTING_PERIOD_SECONDS = "cashback_vesting_period_seconds"
        const val KEY_CASHBACK_VESTING_THRESHOLD = "cashback_vesting_threshold"
        const val KEY_COUNT_NON_MEMBER_VOTES = "count_non_member_votes"
        const val KEY_ALLOW_NON_MEMBER_WHITELISTS = "allow_non_member_whitelists"
        const val KEY_WITNESS_PAY_PER_BLOCK = "witness_pay_per_block"
        const val KEY_WORKER_BUDGET_PER_DAY = "worker_budget_per_day"
        const val KEY_MAX_PREDICATE_OPCODE = "max_predicate_opcode"
        const val KEY_FEE_LIQUIDATION_THRESHOLD = "fee_liquidation_threshold"
        const val KEY_ACCOUNTS_PER_FEE_SCALE = "accounts_per_fee_scale"
        const val KEY_ACCOUNT_FEE_SCALE_BITSHIFTS = "account_fee_scale_bitshifts"
        const val KEY_MAX_AUTHORITY_DEPTH = "max_authority_depth"


        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): ChainParameters {
            return ChainParameters(
                rawJson.optItem(KEY_CURRENT_FEE),
                rawJson.optUByte(KEY_BLOCK_INTERVAL),
                rawJson.optUInt(KEY_MAINTENANCE_INTERVAL),
                rawJson.optUByte(KEY_MAINTENANCE_SKIP_SLOTS),
                rawJson.optUInt(KEY_COMMITTEE_PROPOSAL_REVIEW_PERIOD),
                rawJson.optUInt(KEY_MAXIMUM_TRANSACTION_SIZE),
                rawJson.optUInt(KEY_MAXIMUM_BLOCK_SIZE),
                rawJson.optUInt(KEY_MAXIMUM_TIME_UNTIL_EXPIRATION),
                rawJson.optUInt(KEY_MAXIMUM_PROPOSAL_LIFETIME),
                rawJson.optUByte(KEY_MAXIMUM_ASSET_WHITELIST_AUTHORITIES),
                rawJson.optUByte(KEY_MAXIMUM_ASSET_FEED_PUBLISHERS),
                rawJson.optUShort(KEY_MAXIMUM_WITNESS_COUNT),
                rawJson.optUShort(KEY_MAXIMUM_COMMITTEE_COUNT),
                rawJson.optUShort(KEY_MAXIMUM_AUTHORITY_MEMBERSHIP),
                rawJson.optUShort(KEY_RESERVE_PERCENT_OF_FEE),
                rawJson.optUShort(KEY_NETWORK_PERCENT_OF_FEE),
                rawJson.optUShort(KEY_LIFETIME_REFERRER_PERCENT_OF_FEE),
                rawJson.optUInt(KEY_CASHBACK_VESTING_PERIOD_SECONDS),
                rawJson.optLong(KEY_CASHBACK_VESTING_THRESHOLD),
                rawJson.optBoolean(KEY_COUNT_NON_MEMBER_VOTES),
                rawJson.optBoolean(KEY_ALLOW_NON_MEMBER_WHITELISTS),
                rawJson.optLong(KEY_WITNESS_PAY_PER_BLOCK),
                rawJson.optLong(KEY_WORKER_BUDGET_PER_DAY),
                rawJson.optUShort(KEY_MAX_PREDICATE_OPCODE),
                rawJson.optLong(KEY_FEE_LIQUIDATION_THRESHOLD),
                rawJson.optUShort(KEY_ACCOUNTS_PER_FEE_SCALE),
                rawJson.optUByte(KEY_ACCOUNT_FEE_SCALE_BITSHIFTS),
                rawJson.optUByte(KEY_MAX_AUTHORITY_DEPTH)
            )
        }
    }


    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
    }

}