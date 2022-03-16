package graphene.protocol


/** percentage fields are fixed point with a denominator of 10,000 */
const val GRAPHENE_100_PERCENT                                          = 10000
const val GRAPHENE_1_PERCENT                                            = GRAPHENE_100_PERCENT / 100

const val GRAPHENE_SYMBOL                                               = "BTS"
const val GRAPHENE_ADDRESS_PREFIX                                       = "BTS"

const val GRAPHENE_BLOCKCHAIN_PRECISION: UInt64                         = 100000U
const val GRAPHENE_BLOCKCHAIN_PRECISION_DIGITS                          = 5

const val GRAPHENE_MIN_ACCOUNT_NAME_LENGTH                              = 1
const val GRAPHENE_MAX_ACCOUNT_NAME_LENGTH                              = 63

const val GRAPHENE_MIN_ASSET_SYMBOL_LENGTH                              = 3
const val GRAPHENE_MAX_ASSET_SYMBOL_LENGTH                              = 16

const val GRAPHENE_MAX_SHARE_SUPPLY: UInt64                             = 1000000000000000U // 10 ^ 15

const val GRAPHENE_MAX_WORKER_NAME_LENGTH                               = 63
const val GRAPHENE_MAX_URL_LENGTH                                       = 127

const val GRAPHENE_MAX_SIG_CHECK_DEPTH                                  = 2

//const val GRAPHENE_IRREVERSIBLE_THRESHOLD = (70 * GRAPHENE_1_PERCENT)

/**
 * every second, the fraction of burned core asset which cycles is
 * GRAPHENE_CORE_ASSET_CYCLE_RATE / (1 << GRAPHENE_CORE_ASSET_CYCLE_RATE_BITS)
 */
const val GRAPHENE_CORE_ASSET_CYCLE_RATE                                = 17
const val GRAPHENE_CORE_ASSET_CYCLE_RATE_BITS                           = 32

/**
 * Don't allow the committee_members to publish a limit that would
 * make the network unable to operate.
 */
const val GRAPHENE_MIN_TRANSACTION_SIZE_LIMIT                           = 1024
const val GRAPHENE_MIN_BLOCK_INTERVAL                                   = 1 /* seconds */
const val GRAPHENE_MAX_BLOCK_INTERVAL                                   = 30 /* seconds */

const val GRAPHENE_DEFAULT_BLOCK_INTERVAL                               = 5 /* seconds */
const val GRAPHENE_DEFAULT_MAX_TRANSACTION_SIZE                         = 2048
const val GRAPHENE_DEFAULT_MAX_BLOCK_SIZE                               = 2 * 1000 * 1000 /* < 2 MiB (less than MAX_MESSAGE_SIZE in graphene/net/config.hpp) */
const val GRAPHENE_DEFAULT_MAX_TIME_UNTIL_EXPIRATION                    = 60 * 60 * 24 // seconds,  aka: 1 day
const val GRAPHENE_DEFAULT_MAINTENANCE_INTERVAL                         = 60 * 60 * 24 // seconds, aka: 1 day
const val GRAPHENE_DEFAULT_MAINTENANCE_SKIP_SLOTS                       = 3  // number of slots to skip for maintenance interval

const val GRAPHENE_DEFAULT_FORCE_SETTLEMENT_DELAY                       = 60 * 60 * 24 ///< 1 day
const val GRAPHENE_DEFAULT_FORCE_SETTLEMENT_OFFSET                      = 0 ///< 1%
      val GRAPHENE_DEFAULT_FORCE_SETTLEMENT_MAX_VOLUME                  = 20* GRAPHENE_1_PERCENT ///< 20%
const val GRAPHENE_DEFAULT_PRICE_FEED_LIFETIME                          = 60 * 60 * 24 ///< 1 day
const val GRAPHENE_DEFAULT_MAX_AUTHORITY_MEMBERSHIP                     = 10
const val GRAPHENE_DEFAULT_MAX_ASSET_WHITELIST_AUTHORITIES              = 10
const val GRAPHENE_DEFAULT_MAX_ASSET_FEED_PUBLISHERS                    = 10

const val GRAPHENE_DEFAULT_MIN_WITNESS_COUNT                            = 11
const val GRAPHENE_DEFAULT_MIN_COMMITTEE_MEMBER_COUNT                   = 11
const val GRAPHENE_DEFAULT_MAX_WITNESSES                                = 1001 // SHOULD BE ODD
const val GRAPHENE_DEFAULT_MAX_COMMITTEE                                = 1001 // SHOULD BE ODD
const val GRAPHENE_DEFAULT_MAX_PROPOSAL_LIFETIME_SEC                    = 60 * 60 * 24 * 7 * 4 // Four weeks
const val GRAPHENE_DEFAULT_COMMITTEE_PROPOSAL_REVIEW_PERIOD_SEC         = 60 * 60 * 24 * 7 * 2 // Two weeks
const val GRAPHENE_DEFAULT_NETWORK_PERCENT_OF_FEE                       = 20 * GRAPHENE_1_PERCENT
const val GRAPHENE_DEFAULT_LIFETIME_REFERRER_PERCENT_OF_FEE             = 30 * GRAPHENE_1_PERCENT
const val GRAPHENE_DEFAULT_CASHBACK_VESTING_PERIOD_SEC                  = 60 * 60 * 24 * 365 ///< 1 year
      val GRAPHENE_DEFAULT_CASHBACK_VESTING_THRESHOLD                   = GRAPHENE_BLOCKCHAIN_PRECISION * 100U // TODO
const val GRAPHENE_DEFAULT_BURN_PERCENT_OF_FEE                          = 20 * GRAPHENE_1_PERCENT
const val GRAPHENE_DEFAULT_MAX_ASSERT_OPCODE                            = 1
      val GRAPHENE_DEFAULT_FEE_LIQUIDATION_THRESHOLD                    = GRAPHENE_BLOCKCHAIN_PRECISION * 100U // TODO
const val GRAPHENE_DEFAULT_ACCOUNTS_PER_FEE_SCALE                       = 1000
const val GRAPHENE_DEFAULT_ACCOUNT_FEE_SCALE_BITSHIFTS                  = 4
const val GRAPHENE_DEFAULT_MAX_BUYBACK_MARKETS                          = 4

      val GRAPHENE_DEFAULT_WITNESS_PAY_PER_BLOCK                        = GRAPHENE_BLOCKCHAIN_PRECISION * 10U
const val GRAPHENE_DEFAULT_WITNESS_PAY_VESTING_SECONDS                  = 60 * 60 * 24
      val GRAPHENE_DEFAULT_WORKER_BUDGET_PER_DAY                        = GRAPHENE_BLOCKCHAIN_PRECISION * 500U * 1000U
const val GRAPHENE_DEFAULT_MINIMUM_FEEDS                                = 7

const val GRAPHENE_MIN_BLOCK_SIZE_LIMIT                                 = GRAPHENE_MIN_TRANSACTION_SIZE_LIMIT * 5 // 5 transactions per block

/** NOTE: making this a power of 2 (say 2^15) would greatly accelerate fee calcs */

const val GRAPHENE_MAX_MARKET_FEE_PERCENT                               = GRAPHENE_100_PERCENT
/**
 *  These ratios are fixed point numbers with a denominator of GRAPHENE_COLLATERAL_RATIO_DENOM, the
 *  minimum maitenance collateral is therefore 1.001x and the default
 *  maintenance ratio is 1.75x
 */
const val GRAPHENE_COLLATERAL_RATIO_DENOM                               = 1000
const val GRAPHENE_MIN_COLLATERAL_RATIO                                 = 1001  ///< lower than this could result in divide by 0
const val GRAPHENE_MAX_COLLATERAL_RATIO                                 = 32000 ///< higher than this is unnecessary and may exceed int16 storage
const val GRAPHENE_DEFAULT_MAINTENANCE_COLLATERAL_RATIO                 = 1750 ///< Call when collateral only pays off 175% the debt
const val GRAPHENE_DEFAULT_MAX_SHORT_SQUEEZE_RATIO                      = 1500 ///< Stop calling when collateral only pays off 150% of the debt

// Denominator for SameT Fund fee calculation
const val GRAPHENE_FEE_RATE_DENOM: UInt32                               = 1000000U

// How long a credit offer will be kept active, in days
const val GRAPHENE_MAX_CREDIT_OFFER_DAYS: Int64                         = 380
// How long a credit offer will be kept active, in seconds
const val GRAPHENE_MAX_CREDIT_OFFER_SECS: Int64                         = GRAPHENE_MAX_CREDIT_OFFER_DAYS * 86400
// How long a credit deal will be kept, in days
const val GRAPHENE_MAX_CREDIT_DEAL_DAYS: Int64                          = 380
// How long a credit deal will be kept, in seconds
const val GRAPHENE_MAX_CREDIT_DEAL_SECS: Int64                          = GRAPHENE_MAX_CREDIT_DEAL_DAYS * 86400

// How many iterations to run in @c fee_schedule::set_fee()
const val MAX_FEE_STABILIZATION_ITERATION: UInt64 /* size_t */          = 4U

/**
 *  Reserved Account IDs with special meaning
 */
// Represents the current committee members, two-week review period
val GRAPHENE_COMMITTEE_ACCOUNT                                          = K102_AccountIdType(0U)
// Represents the current witnesses
val GRAPHENE_WITNESS_ACCOUNT                                            = K102_AccountIdType(1U)
// Represents the current committee members
val GRAPHENE_RELAXED_COMMITTEE_ACCOUNT                                  = K102_AccountIdType(2U)
// Represents the canonical account with NO authority (nobody can access funds in null account)
val GRAPHENE_NULL_ACCOUNT                                               = K102_AccountIdType(3U)
// Represents the canonical account with WILDCARD authority (anybody can access funds in temp account)
val GRAPHENE_TEMP_ACCOUNT                                               = K102_AccountIdType(4U)
// Represents the canonical account for specifying you will vote directly (as opposed to a proxy)
val GRAPHENE_PROXY_TO_SELF_ACCOUNT                                      = K102_AccountIdType(5U)
/// Sentinel value used in the scheduler.
val GRAPHENE_NULL_WITNESS                                               = K106_WitnessIdType(0U)


val GRAPHENE_FBA_STEALTH_DESIGNATED_ASSET                               = K103_AssetIdType(743U)

// Maximum duration before a custom authority can expire (1 month)
const val GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITY_LIFETIME_SECONDS        = 60*60*24*30
// Maximum number of custom authorities a particular account can set
const val GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITIES_PER_ACCOUNT           = 10
// Maximum number of custom authorities a particular account can set for a particular operation
const val GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITIES_PER_ACCOUNT_OP        = 3
// Maximum number of restrictions a custom authority can contain
const val GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITY_RESTRICTIONS            = 10