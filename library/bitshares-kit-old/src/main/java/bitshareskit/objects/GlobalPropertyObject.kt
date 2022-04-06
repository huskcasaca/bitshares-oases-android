package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.extensions.createGraphene
import bitshareskit.extensions.optIterable
import bitshareskit.models.FeeParameters
import org.java_json.JSONArray
import org.java_json.JSONObject

@Entity(tableName = GlobalPropertyObject.TABLE_NAME)
data class GlobalPropertyObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    companion object {
        @Ignore const val TABLE_NAME = "global_property_object"

        @Ignore const val KEY_PARAMETERS = "parameters"
        @Ignore const val KEY_CURRENT_FEES = "current_fees"
        @Ignore const val KEY_BLOCK_INTERVAL = "block_interval"
        @Ignore const val KEY_MAINTENANCE_INTERVAL = "maintenance_interval"
        @Ignore const val KEY_MAINTENANCE_SKIP_SLOTS = "maintenance_skip_slots"
        @Ignore const val KEY_COMMITTEE_PROPOSAL_REVIEW_PERIOD = "committee_proposal_review_period"
        @Ignore const val KEY_MAXIMUM_TRANSACTION_SIZE = "maximum_transaction_size"
        @Ignore const val KEY_MAXIMUM_BLOCK_SIZE = "maximum_block_size"
        @Ignore const val KEY_MAXIMUM_TIME_UNTIL_EXPIRATION = "maximum_time_until_expiration"
        @Ignore const val KEY_MAXIMUM_PROPOSAL_LIFETIME = "maximum_proposal_lifetime"
        @Ignore const val KEY_MAXIMUM_ASSET_WHITELIST_AUTHORITIES = "maximum_asset_whitelist_authorities"
        @Ignore const val KEY_MAXIMUM_ASSET_FEED_PUBLISHERS = "maximum_asset_feed_publishers"
        @Ignore const val KEY_MAXIMUM_WITNESS_COUNT = "maximum_witness_count"
        @Ignore const val KEY_MAXIMUM_COMMITTEE_COUNT = "maximum_committee_count"
        @Ignore const val KEY_MAXIMUM_AUTHORITY_MEMBERSHIP = "maximum_authority_membership"
        @Ignore const val KEY_RESERVE_PERCENT_OF_FEE = "reserve_percent_of_fee"
        @Ignore const val KEY_NETWORK_PERCENT_OF_FEE = "network_percent_of_fee"
        @Ignore const val KEY_LIFETIME_REFERRER_PERCENT_OF_FEE = "lifetime_referrer_percent_of_fee"
        @Ignore const val KEY_CASHBACK_VESTING_PERIOD_SECONDS = "cashback_vesting_period_seconds"
        @Ignore const val KEY_CASHBACK_VESTING_THRESHOLD = "cashback_vesting_threshold"
        @Ignore const val KEY_COUNT_NON_MEMBER_VOTES = "count_non_member_votes"
        @Ignore const val KEY_ALLOW_NON_MEMBER_WHITELISTS = "allow_non_member_whitelists"
        @Ignore const val KEY_WITNESS_PAY_PER_BLOCK = "witness_pay_per_block"
        @Ignore const val KEY_WORKER_BUDGET_PER_DAY = "worker_budget_per_day"
        @Ignore const val KEY_MAX_PREDICATE_OPCODE = "max_predicate_opcode"
        @Ignore const val KEY_FEE_LIQUIDATION_THRESHOLD = "fee_liquidation_threshold"
        @Ignore const val KEY_ACCOUNTS_PER_FEE_SCALE = "accounts_per_fee_scale"
        @Ignore const val KEY_ACCOUNT_FEE_SCALE_BITSHIFTS = "account_fee_scale_bitshifts"
        @Ignore const val KEY_MAX_AUTHORITY_DEPTH = "max_authority_depth"
        @Ignore const val KEY_EXTENSIONS = "extensions"
        @Ignore const val KEY_UPDATABLE_HTLC_OPTIONS = "updatable_htlc_options"
        @Ignore const val KEY_MAX_TIMEOUT_SECS = "max_timeout_secs"
        @Ignore const val KEY_MAX_PREIMAGE_SIZE = "max_preimage_size"
        @Ignore const val KEY_NEXT_AVAILABLE_VOTE_ID = "next_available_vote_id"
        @Ignore const val KEY_ACTIVE_COMMITTEE_MEMBERS = "active_committee_members"
        @Ignore const val KEY_ACTIVE_WITNESSES = "active_witnesses"

    }
/*

{
    "id": "2.0.0",
    "parameters": {
        "current_fees": {
            "parameters": [],
            "scale": 10000
        },
        "block_interval": 3,
        "maintenance_interval": 3600,
        "maintenance_skip_slots": 3,
        "committee_proposal_review_period": 3600,
        "maximum_transaction_size": 409600,
        "maximum_block_size": 2000000,
        "maximum_time_until_expiration": 86400,
        "maximum_proposal_lifetime": 2419200,
        "maximum_asset_whitelist_authorities": 10,
        "maximum_asset_feed_publishers": 25,
        "maximum_witness_count": 1001,
        "maximum_committee_count": 1001,
        "maximum_authority_membership": 10,
        "reserve_percent_of_fee": 2000,
        "network_percent_of_fee": 2000,
        "lifetime_referrer_percent_of_fee": 3000,
        "cashback_vesting_period_seconds": 7776000,
        "cashback_vesting_threshold": 10000000,
        "count_non_member_votes": true,
        "allow_non_member_whitelists": false,
        "witness_pay_per_block": 100000,
        "worker_budget_per_day": "50000000000",
        "max_predicate_opcode": 1,
        "fee_liquidation_threshold": 10000000,
        "accounts_per_fee_scale": 1000,
        "account_fee_scale_bitshifts": 0,
        "max_authority_depth": 2,
        "extensions": {
            "updatable_htlc_options": {
                "max_timeout_secs": 2592000,
                "max_preimage_size": 10240
            }
        }
    },
    "next_available_vote_id": 739,
    "active_committee_members": [],
    "active_witnesses": []
}
*/

    @Ignore val workerBudgetPerDay: Long

    @Ignore val activeCommitteeMembers: List<CommitteeMemberObject>
    @Ignore val activeWitnesses: List<WitnessObject>
    @Ignore val currentFees: List<FeeParameters>


    init {
        val parameters = rawJson.optJSONObject(KEY_PARAMETERS)
        workerBudgetPerDay = parameters.optLong(KEY_WORKER_BUDGET_PER_DAY)
        activeCommitteeMembers = rawJson.optIterable<String>(KEY_ACTIVE_COMMITTEE_MEMBERS).map { createGraphene(it) }
        activeWitnesses = rawJson.optIterable<String>(KEY_ACTIVE_WITNESSES).map { createGraphene(it) }
        currentFees = parameters.optJSONObject(KEY_CURRENT_FEES).optIterable<JSONArray>(KEY_PARAMETERS).map { FeeParameters.fromJsonPair(it) }
    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()



}

/*

{
    "id": "2.0.0",
    "parameters": {
        "current_fees": {
            "parameters": [
                [
                    0,
                    {
                        "fee": 86869,
                        "price_per_kbyte": 48260
                    }],
                [
                    1,
                    {
                        "fee": 4826
                    }],
                [
                    2,
                    {
                        "fee": 482
                    }],
                [
                    3,
                    {
                        "fee": 48260
                    }],
                [
                    4,
                    {
                    }],
                [
                    5,
                    {
                        "basic_fee": 482609,
                        "premium_fee": 24130471,
                        "price_per_kbyte": 33782
                    }],
                [
                    6,
                    {
                        "fee": 4826,
                        "price_per_kbyte": 33782
                    }],
                [
                    7,
                    {
                        "fee": 482609
                    }],
                [
                    8,
                    {
                        "membership_annual_fee": "477783329067000",
                        "membership_lifetime_fee": 579131307
                    }],
                [
                    9,
                    {
                        "fee": 24130471
                    }],
                [
                    10,
                    {
                        "symbol3": "38608753864",
                        "symbol4": "9652188466",
                        "long_symbol": 241304711,
                        "price_per_kbyte": 48260
                    }],
                [
                    11,
                    {
                        "fee": 9652188,
                        "price_per_kbyte": 33782
                    }],
                [
                    12,
                    {
                        "fee": 24130471
                    }],
                [
                    13,
                    {
                        "fee": 24130471
                    }],
                [
                    14,
                    {
                        "fee": 86869,
                        "price_per_kbyte": 48260
                    }],
                [
                    15,
                    {
                        "fee": 4826
                    }],
                [
                    16,
                    {
                        "fee": 2413047
                    }],
                [
                    17,
                    {
                        "fee": 965218
                    }],
                [
                    18,
                    {
                        "fee": 24130471
                    }],
                [
                    19,
                    {
                        "fee": 482
                    }],
                [
                    20,
                    {
                        "fee": 241304711
                    }],
                [
                    21,
                    {
                        "fee": 48260
                    }],
                [
                    22,
                    {
                        "fee": 965218,
                        "price_per_kbyte": 241304
                    }],
                [
                    23,
                    {
                        "fee": 24130,
                        "price_per_kbyte": 33782
                    }],
                [
                    24,
                    {
                        "fee": 0
                    }],
                [
                    25,
                    {
                        "fee": 723914
                    }],
                [
                    26,
                    {
                        "fee": 48260
                    }],
                [
                    27,
                    {
                        "fee": 69495,
                        "price_per_kbyte": 33782
                    }],
                [
                    28,
                    {
                        "fee": 0
                    }],
                [
                    29,
                    {
                        "fee": 24130471
                    }],
                [
                    30,
                    {
                        "fee": 48260942
                    }],
                [
                    31,
                    {
                        "fee": 0
                    }],
                [
                    32,
                    {
                        "fee": 482609
                    }],
                [
                    33,
                    {
                        "fee": 482609
                    }],
                [
                    34,
                    {
                        "fee": 241304711
                    }],
                [
                    35,
                    {
                        "fee": 48260,
                        "price_per_kbyte": 241304
                    }],
                [
                    36,
                    {
                        "fee": 2413047
                    }],
                [
                    37,
                    {
                    }],
                [
                    38,
                    {
                        "fee": 4826094,
                        "price_per_kbyte": 33782
                    }],
                [
                    39,
                    {
                        "fee": 1013479,
                        "price_per_output": 337826
                    }],
                [
                    41,
                    {
                        "fee": 1013479
                    }],
                [
                    43,
                    {
                        "fee": 4826094
                    }],
                [
                    44,
                    {
                    }],
                [
                    45,
                    {
                        "fee": 48260
                    }],
                [
                    46,
                    {
                    }],
                [
                    47,
                    {
                        "fee": 24130471
                    }],
                [
                    48,
                    {
                        "fee": 48260942
                    }],
                [
                    49,
                    {
                        "fee": 4826,
                        "fee_per_day": 159261
                    }],
                [
                    50,
                    {
                        "fee": 4826,
                        "fee_per_kb": 482609
                    }],
                [
                    52,
                    {
                        "fee": 4826,
                        "fee_per_day": 159261
                    }]
            ],
            "scale": 10000
        },
        "block_interval": 3,
        "maintenance_interval": 3600,
        "maintenance_skip_slots": 3,
        "committee_proposal_review_period": 3600,
        "maximum_transaction_size": 409600,
        "maximum_block_size": 2000000,
        "maximum_time_until_expiration": 86400,
        "maximum_proposal_lifetime": 2419200,
        "maximum_asset_whitelist_authorities": 10,
        "maximum_asset_feed_publishers": 25,
        "maximum_witness_count": 1001,
        "maximum_committee_count": 1001,
        "maximum_authority_membership": 10,
        "reserve_percent_of_fee": 2000,
        "network_percent_of_fee": 2000,
        "lifetime_referrer_percent_of_fee": 3000,
        "cashback_vesting_period_seconds": 7776000,
        "cashback_vesting_threshold": 10000000,
        "count_non_member_votes": true,
        "allow_non_member_whitelists": false,
        "witness_pay_per_block": 100000,
        "worker_budget_per_day": "50000000000",
        "max_predicate_opcode": 1,
        "fee_liquidation_threshold": 10000000,
        "accounts_per_fee_scale": 1000,
        "account_fee_scale_bitshifts": 0,
        "max_authority_depth": 2,
        "extensions": {
            "updatable_htlc_options": {
                "max_timeout_secs": 2592000,
                "max_preimage_size": 10240
            }
        }
    },
    "next_available_vote_id": 739,
    "active_committee_members": [
        "1.5.25",
        "1.5.15",
        "1.5.40",
        "1.5.56",
        "1.5.16",
        "1.5.52",
        "1.5.50",
        "1.5.37",
        "1.5.51",
        "1.5.35",
        "1.5.49"],
    "active_witnesses": [
        "1.6.17",
        "1.6.28",
        "1.6.35",
        "1.6.48",
        "1.6.59",
        "1.6.63",
        "1.6.65",
        "1.6.71",
        "1.6.74",
        "1.6.76",
        "1.6.98",
        "1.6.105",
        "1.6.108",
        "1.6.110",
        "1.6.116",
        "1.6.117",
        "1.6.120",
        "1.6.129",
        "1.6.131",
        "1.6.151",
        "1.6.157"]
}

 */