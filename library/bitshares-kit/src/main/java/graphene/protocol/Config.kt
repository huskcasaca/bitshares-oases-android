package graphene.protocol


/** percentage fields are fixed point with a denominator of 10,000 */
val GRAPHENE_100_PERCENT: UInt32                                        = 10000U
val GRAPHENE_1_PERCENT: UInt32                                          = GRAPHENE_100_PERCENT / 100U

val GRAPHENE_SYMBOL: String                                             = "BTS"
val GRAPHENE_ADDRESS_PREFIX: String                                     = "BTS"

val GRAPHENE_BLOCKCHAIN_PRECISION: UInt32                               = 100000U
val GRAPHENE_BLOCKCHAIN_PRECISION_DIGITS: UInt32                        = 5U

val GRAPHENE_MIN_ACCOUNT_NAME_LENGTH: UInt32                            = 1U
val GRAPHENE_MAX_ACCOUNT_NAME_LENGTH: UInt32                            = 63U

val GRAPHENE_MIN_ASSET_SYMBOL_LENGTH: UInt32                            = 3U
val GRAPHENE_MAX_ASSET_SYMBOL_LENGTH: UInt32                            = 16U

val GRAPHENE_MAX_SHARE_SUPPLY: UInt64                                   = 1000000000000000U // 10 ^ 15

val GRAPHENE_MAX_WORKER_NAME_LENGTH: UInt32                             = 63U
val GRAPHENE_MAX_URL_LENGTH: UInt32                                     = 127U

val GRAPHENE_MAX_SIG_CHECK_DEPTH: UInt8                                 = 2U

//const val GRAPHENE_IRREVERSIBLE_THRESHOLD = (70 * GRAPHENE_1_PERCENT)

/**
 * every second, the fraction of burned core asset which cycles is
 * GRAPHENE_CORE_ASSET_CYCLE_RATE / (1 << GRAPHENE_CORE_ASSET_CYCLE_RATE_BITS)
 */
val GRAPHENE_CORE_ASSET_CYCLE_RATE: UInt32                              = 17U
val GRAPHENE_CORE_ASSET_CYCLE_RATE_BITS: UInt32                         = 32U

/**
 * Don't allow the committee_members to publish a limit that would
 * make the network unable to operate.
 */
val GRAPHENE_MIN_TRANSACTION_SIZE_LIMIT: UInt32                         = 1024U
val GRAPHENE_MIN_BLOCK_INTERVAL: UInt32                                 = 1U /* seconds */
val GRAPHENE_MAX_BLOCK_INTERVAL: UInt32                                 = 30U /* seconds */

val GRAPHENE_DEFAULT_BLOCK_INTERVAL: UInt8                              = 5U /* seconds */
val GRAPHENE_DEFAULT_MAX_TRANSACTION_SIZE: UInt32                       = 2048U
val GRAPHENE_DEFAULT_MAX_BLOCK_SIZE: UInt32                             = 2U * 1000U * 1000U /* < 2 MiB (less than MAX_MESSAGE_SIZE in graphene/net/config.hpp) */
val GRAPHENE_DEFAULT_MAX_TIME_UNTIL_EXPIRATION: UInt32                  = 60U * 60U * 24U // seconds,  aka: 1 day
val GRAPHENE_DEFAULT_MAINTENANCE_INTERVAL: UInt32                       = 60U * 60U * 24U // seconds, aka: 1 day
val GRAPHENE_DEFAULT_MAINTENANCE_SKIP_SLOTS: UInt8                      = 3U  // number of slots to skip for maintenance interval

val GRAPHENE_DEFAULT_FORCE_SETTLEMENT_DELAY: UInt32                     = 60U * 60U * 24U ///< 1 day
val GRAPHENE_DEFAULT_FORCE_SETTLEMENT_OFFSET: UInt16                    = 0U ///< 1%
val GRAPHENE_DEFAULT_FORCE_SETTLEMENT_MAX_VOLUME: UInt16                = (20U * GRAPHENE_1_PERCENT).toUInt16() ///< 20%
val GRAPHENE_DEFAULT_PRICE_FEED_LIFETIME: UInt32                        = 60U * 60U * 24U ///< 1 day
val GRAPHENE_DEFAULT_MAX_AUTHORITY_MEMBERSHIP: UInt16                   = 10U
val GRAPHENE_DEFAULT_MAX_ASSET_WHITELIST_AUTHORITIES: UInt8             = 10U
val GRAPHENE_DEFAULT_MAX_ASSET_FEED_PUBLISHERS: UInt8                   = 10U

val GRAPHENE_DEFAULT_MIN_WITNESS_COUNT: UInt16                          = 11U
val GRAPHENE_DEFAULT_MIN_COMMITTEE_MEMBER_COUNT: UInt16                 = 11U
val GRAPHENE_DEFAULT_MAX_WITNESSES: UInt16                              = 1001U // SHOULD BE ODD
val GRAPHENE_DEFAULT_MAX_COMMITTEE: UInt16                              = 1001U // SHOULD BE ODD
val GRAPHENE_DEFAULT_MAX_PROPOSAL_LIFETIME_SEC: UInt32                  = 60U * 60U * 24U * 7U * 4U // Four weeks
val GRAPHENE_DEFAULT_COMMITTEE_PROPOSAL_REVIEW_PERIOD_SEC: UInt32       = 60U * 60U * 24U * 7U * 2U // Two weeks
val GRAPHENE_DEFAULT_NETWORK_PERCENT_OF_FEE: UInt16                     = (20U * GRAPHENE_1_PERCENT).toUInt16()
val GRAPHENE_DEFAULT_LIFETIME_REFERRER_PERCENT_OF_FEE: UInt16           = (30U * GRAPHENE_1_PERCENT).toUInt16()
val GRAPHENE_DEFAULT_CASHBACK_VESTING_PERIOD_SEC: UInt32                = 60U * 60U * 24U * 365U ///< 1 year
val GRAPHENE_DEFAULT_CASHBACK_VESTING_THRESHOLD: ShareType              = (100UL * GRAPHENE_BLOCKCHAIN_PRECISION).toInt64() // TODO
val GRAPHENE_DEFAULT_BURN_PERCENT_OF_FEE: UInt16                        = (20U * GRAPHENE_1_PERCENT).toUInt16()
val GRAPHENE_DEFAULT_MAX_ASSERT_OPCODE: UInt16                          = 1U
val GRAPHENE_DEFAULT_FEE_LIQUIDATION_THRESHOLD: ShareType               = (100UL * GRAPHENE_BLOCKCHAIN_PRECISION).toInt64() // TODO
val GRAPHENE_DEFAULT_ACCOUNTS_PER_FEE_SCALE: UInt16                     = 1000U
val GRAPHENE_DEFAULT_ACCOUNT_FEE_SCALE_BITSHIFTS: UInt8                 = 4U
val GRAPHENE_DEFAULT_MAX_BUYBACK_MARKETS: UInt8                         = 4U

val GRAPHENE_DEFAULT_WITNESS_PAY_PER_BLOCK: ShareType                   = (10UL * GRAPHENE_BLOCKCHAIN_PRECISION).toInt64()
val GRAPHENE_DEFAULT_WITNESS_PAY_VESTING_SECONDS: UInt32                = 60U * 60U * 24U
val GRAPHENE_DEFAULT_WORKER_BUDGET_PER_DAY: ShareType                   = (500UL * 1000UL * GRAPHENE_BLOCKCHAIN_PRECISION).toInt64()
val GRAPHENE_DEFAULT_MINIMUM_FEEDS: UInt8                               = 7U

val GRAPHENE_MIN_BLOCK_SIZE_LIMIT: UInt32                               = GRAPHENE_MIN_TRANSACTION_SIZE_LIMIT * 5U // 5 transactions per block

/** NOTE: making this a power of 2 (say 2^15) would greatly accelerate fee calcs */

val GRAPHENE_MAX_MARKET_FEE_PERCENT: UInt32                             = GRAPHENE_100_PERCENT
/**
 *  These ratios are fixed point numbers with a denominator of GRAPHENE_COLLATERAL_RATIO_DENOM, the
 *  minimum maitenance collateral is therefore 1.001x and the default
 *  maintenance ratio is 1.75x
 */
val GRAPHENE_COLLATERAL_RATIO_DENOM: UInt16                             = 1000U
val GRAPHENE_MIN_COLLATERAL_RATIO: UInt16                               = 1001U  ///< lower than this could result in divide by 0
val GRAPHENE_MAX_COLLATERAL_RATIO: UInt16                               = 32000U ///< higher than this is unnecessary and may exceed int16 storage
val GRAPHENE_DEFAULT_MAINTENANCE_COLLATERAL_RATIO: UInt16               = 1750U ///< Call when collateral only pays off 175% the debt
val GRAPHENE_DEFAULT_MAX_SHORT_SQUEEZE_RATIO: UInt16                    = 1500U ///< Stop calling when collateral only pays off 150% of the debt

// Denominator for SameT Fund fee calculation
val GRAPHENE_FEE_RATE_DENOM: UInt32                                     = 1000000U

// How long a credit offer will be kept active, in days
val GRAPHENE_MAX_CREDIT_OFFER_DAYS: Int64                               = 380
// How long a credit offer will be kept active, in seconds
val GRAPHENE_MAX_CREDIT_OFFER_SECS: Int64                               = GRAPHENE_MAX_CREDIT_OFFER_DAYS * 86400
// How long a credit deal will be kept, in days
val GRAPHENE_MAX_CREDIT_DEAL_DAYS: Int64                                = 380
// How long a credit deal will be kept, in seconds
val GRAPHENE_MAX_CREDIT_DEAL_SECS: Int64                                = GRAPHENE_MAX_CREDIT_DEAL_DAYS * 86400

// How many iterations to run in @c fee_schedule::set_fee()
val MAX_FEE_STABILIZATION_ITERATION: UInt64 /* size_t */                = 4U

/**
 *  Reserved Account IDs with special meaning
 */
// Represents the current committee members, two-week review period
val GRAPHENE_COMMITTEE_ACCOUNT                                          = AccountId(0U)
// Represents the current witnesses
val GRAPHENE_WITNESS_ACCOUNT                                            = AccountId(1U)
// Represents the current committee members
val GRAPHENE_RELAXED_COMMITTEE_ACCOUNT                                  = AccountId(2U)
// Represents the canonical account with NO authority (nobody can access funds in null account)
val GRAPHENE_NULL_ACCOUNT                                               = AccountId(3U)
// Represents the canonical account with WILDCARD authority (anybody can access funds in temp account)
val GRAPHENE_TEMP_ACCOUNT                                               = AccountId(4U)
// Represents the canonical account for specifying you will vote directly (as opposed to a proxy)
val GRAPHENE_PROXY_TO_SELF_ACCOUNT                                      = AccountId(5U)
// Sentinel value used in the scheduler.
val GRAPHENE_NULL_WITNESS                                               = WitnessId(0U)


val GRAPHENE_FBA_STEALTH_DESIGNATED_ASSET                               = AssetId(743U)

// Maximum duration before a custom authority can expire (1 month)
val GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITY_LIFETIME_SECONDS: UInt32      = 60U * 60U * 24U * 30U
// Maximum number of custom authorities a particular account can set
val GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITIES_PER_ACCOUNT: UInt32         = 10U
// Maximum number of custom authorities a particular account can set for a particular operation
val GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITIES_PER_ACCOUNT_OP: UInt32      = 3U
// Maximum number of restrictions a custom authority can contain
val GRAPHENE_DEFAULT_MAX_CUSTOM_AUTHORITY_RESTRICTIONS: UInt32          = 10U