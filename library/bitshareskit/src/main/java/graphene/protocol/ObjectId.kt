package graphene.protocol

import bitshareskit.extensions.EMPTY_SPACE
import bitshareskit.extensions.logloglog
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

enum class ObjectSpace(val id: UInt8) {
    /* 0.x.x  */ RELATIVE_PROTOCOL           (0U),
    /* 1.x.x  */ PROTOCOL                    (1U),
    /* 2.x.x  */ IMPLEMENTATION              (2U),
    /* 4.x.x  */ ACCOUNT_HISTORY             (4U),
    /* 5.x.x  */ MARKET_HISTORY_SPACE        (5U),
    /* 7.x.x  */ CUSTOM_OPERATIONS           (7U);
}

interface ObjectType {
    val id: UInt8
}

enum class ProtocolType(override val id: UInt8): ObjectType {
    /* 1.0.x  */ NULL                        (0U), // unused
    /* 1.1.x  */ BASE                        (1U), // unused
    /* 1.2.x  */ ACCOUNT                     (2U),
    /* 1.3.x  */ ASSET                       (3U),
    /* 1.4.x  */ FORCE_SETTLEMENT            (4U),
    /* 1.5.x  */ COMMITTEE_MEMBER            (5U),
    /* 1.6.x  */ WITNESS                     (6U),
    /* 1.7.x  */ LIMIT_ORDER                 (7U),
    /* 1.8.x  */ CALL_ORDER                  (8U),
    /* 1.9.x  */ CUSTOM                      (9U),
    /* 1.10.x */ PROPOSAL                    (10U),
    /* 1.11.x */ OPERATION_HISTORY           (11U),
    /* 1.12.x */ WITHDRAW_PERMISSION         (12U),
    /* 1.13.x */ VESTING_BALANCE             (13U),
    /* 1.14.x */ WORKER                      (14U),
    /* 1.15.x */ BALANCE                     (15U),
    /* 1.16.x */ HTLC                        (16U),
    /* 1.17.x */ CUSTOM_AUTHORITY            (17U),
    /* 1.18.x */ TICKET                      (18U),
    /* 1.19.x */ LIQUIDITY_POOL              (19U),
    /* 1.20.x */ SAMET_FUND                  (20U),
    /* 1.21.x */ CREDIT_OFFER                (21U),
    /* 1.22.x */ CREDIT_DEAL                 (22U);
}

enum class ImplementationType(override val id: UInt8): ObjectType {
    /* 2.0.x  */ GLOBAL_PROPERTY             (0U),
    /* 2.1.x  */ DYNAMIC_GLOBAL_PROPERTY     (1U),
    /* 2.2.x  */ RESERVED                    (2U), // unused
    /* 2.3.x  */ ASSET_DYNAMIC_DATA          (3U),
    /* 2.4.x  */ ASSET_BITASSET_DATA         (4U),
    /* 2.5.x  */ ACCOUNT_BALANCE             (5U),
    /* 2.6.x  */ ACCOUNT_STATISTICS          (6U),
    /* 2.7.x  */ TRANSACTION_HISTORY         (7U),
    /* 2.8.x  */ BLOCK_SUMMARY               (8U),
    /* 2.9.x  */ ACCOUNT_TRANSACTION_HISTORY (9U),
    /* 2.10.x */ BLINDED_BALANCE             (10U),
    /* 2.11.x */ CHAIN_PROPERTY              (11U),
    /* 2.12.x */ WITNESS_SCHEDULE            (12U),
    /* 2.13.x */ BUDGET_RECORD               (13U),
    /* 2.14.x */ SPECIAL_AUTHORITY           (14U),
    /* 2.15.x */ BUYBACK                     (15U),
    /* 2.16.x */ FBA_ACCUMULATOR             (16U),
    /* 2.17.x */ COLLATERAL_BID              (17U),
    /* 2.18.x */ CREDIT_DEAL_SUMMARY         (18U),
}

typealias ObjectInstance = UInt64

val INVALID_ID: ULong = UInt64.MAX_VALUE


val GRAPHENE_ID_TO_SPACE: Map<UInt8, ObjectSpace> =
    ObjectSpace.values().associateBy { it.id }

val GRAPHENE_ID_TO_PROTOCOL_TYPE: Map<UInt8, ProtocolType> =
    ProtocolType.values().associateBy { it.id }

val GRAPHENE_ID_TO_IMPLEMENTATION_TYPE: Map<UInt8, ObjectType> =
    ImplementationType.values().associateBy { it.id }

val GRAPHENE_SPACE_TO_TYPE: Map<ObjectSpace, Map<UInt8, ObjectType>> = mapOf(
    ObjectSpace.PROTOCOL to GRAPHENE_ID_TO_PROTOCOL_TYPE,
    ObjectSpace.IMPLEMENTATION to GRAPHENE_ID_TO_IMPLEMENTATION_TYPE,
)

val GRAPHENE_TYPE_TO_ID_CONSTRUCTOR: Map<ObjectType, KFunction<AbstractIdType>> = mapOf(
    ProtocolType.NULL                               to K100_NullIdType::class,
    ProtocolType.BASE                               to K101_BaseIdType::class,
    ProtocolType.ACCOUNT                            to K102_AccountIdType::class,
    ProtocolType.ASSET                              to K103_AssetIdType::class,
    ProtocolType.FORCE_SETTLEMENT                   to K104_ForceSettlementIdType::class,
    ProtocolType.COMMITTEE_MEMBER                   to K105_CommitteeMemberIdType::class,
    ProtocolType.WITNESS                            to K106_WitnessIdType::class,
    ProtocolType.LIMIT_ORDER                        to K107_LimitOrderIdType::class,
    ProtocolType.CALL_ORDER                         to K108_CallOrderIdType::class,
    ProtocolType.CUSTOM                             to K109_CustomIdType::class,
    ProtocolType.PROPOSAL                           to K110_ProposalIdType::class,
    ProtocolType.OPERATION_HISTORY                  to K111_OperationHistoryIdType::class,
    ProtocolType.WITHDRAW_PERMISSION                to K112_WithdrawPermissionIdType::class,
    ProtocolType.VESTING_BALANCE                    to K113_VestingBalanceIdType::class,
    ProtocolType.WORKER                             to K114_WorkerIdType::class,
    ProtocolType.BALANCE                            to K115_BalanceIdType::class,
    ProtocolType.HTLC                               to K116_HtlcIdType::class,
    ProtocolType.CUSTOM_AUTHORITY                   to K117_CustomAuthorityIdType::class,
    ProtocolType.TICKET                             to K118_TicketIdType::class,
    ProtocolType.LIQUIDITY_POOL                     to K119_LiquidityPoolIdType::class,
    ProtocolType.SAMET_FUND                         to K120_SametFundIdType::class,
    ProtocolType.CREDIT_OFFER                       to K121_CreditOfferIdType::class,
    ProtocolType.CREDIT_DEAL                        to K122_CreditDealIdType::class,

    ImplementationType.GLOBAL_PROPERTY              to K200_GlobalPropertyIdType::class,
    ImplementationType.DYNAMIC_GLOBAL_PROPERTY      to K201_DynamicGlobalPropertyIdType::class,
    ImplementationType.RESERVED                     to K202_ReservedIdType::class,
    ImplementationType.ASSET_DYNAMIC_DATA           to K203_AssetDynamicIdType::class,
    ImplementationType.ASSET_BITASSET_DATA          to K204_AssetBitassetIdType::class,
    ImplementationType.ACCOUNT_BALANCE              to K205_AccountBalanceIdType::class,
    ImplementationType.ACCOUNT_STATISTICS           to K206_AccountStatisticsIdType::class,
    ImplementationType.TRANSACTION_HISTORY          to K207_TransactionHistoryIdType::class,
    ImplementationType.BLOCK_SUMMARY                to K208_BlockSummaryIdType::class,
    ImplementationType.ACCOUNT_TRANSACTION_HISTORY  to K209_AccountTransactionHistoryIdType::class,
    ImplementationType.BLINDED_BALANCE              to K210_BlindedBalanceIdType::class,
    ImplementationType.CHAIN_PROPERTY               to K211_ChainPropertyIdType::class,
    ImplementationType.WITNESS_SCHEDULE             to K212_WitnessScheduleIdType::class,
    ImplementationType.BUDGET_RECORD                to K213_BudgetRecordIdType::class,
    ImplementationType.SPECIAL_AUTHORITY            to K214_SpecialAuthorityIdType::class,
    ImplementationType.BUYBACK                      to K215_BuybackIdType::class,
    ImplementationType.FBA_ACCUMULATOR              to K216_FbaAccumulatorIdType::class,
    ImplementationType.COLLATERAL_BID               to K217_CollateralBidIdType::class,
    ImplementationType.CREDIT_DEAL_SUMMARY          to K218_CreditDealSummaryIdType::class,

    ).mapValues { it.value.constructors.first() }

//    ProtocolType.NULL                               to K100_NullIdType::class,
//    ProtocolType.BASE                               to K101_BaseIdType::class,
//    ProtocolType.ACCOUNT                            to K102_AccountIdType::class,
//    ProtocolType.ASSET                              to K103_AssetIdType::class,
//    ProtocolType.FORCE_SETTLEMENT                   to K104_ForceSettlementIdType::class,
//    ProtocolType.COMMITTEE_MEMBER                   to K105_CommitteeMemberIdType::class,
//    ProtocolType.WITNESS                            to K106_WitnessIdType::class,
//    ProtocolType.LIMIT_ORDER                        to K107_LimitOrderIdType::class,
//    ProtocolType.CALL_ORDER                         to K108_CallOrderIdType::class,
//    ProtocolType.CUSTOM                             to K109_CustomIdType::class,
//    ProtocolType.PROPOSAL                           to K110_ProposalIdType::class,
//    ProtocolType.OPERATION_HISTORY                  to K111_OperationHistoryIdType::class,
//    ProtocolType.WITHDRAW_PERMISSION                to K112_WithdrawPermissionIdType::class,
//    ProtocolType.VESTING_BALANCE                    to K113_VestingBalanceIdType::class,
//    ProtocolType.WORKER                             to K114_WorkerIdType::class,
//    ProtocolType.BALANCE                            to K115_BalanceIdType::class,
//    ProtocolType.HTLC                               to K116_HtlcIdType::class,
//    ProtocolType.CUSTOM_AUTHORITY                   to K117_CustomAuthorityIdType::class,
//    ProtocolType.TICKET                             to K118_TicketIdType::class,
//    ProtocolType.LIQUIDITY_POOL                     to K119_LiquidityPoolIdType::class,
//    ProtocolType.SAMET_FUND                         to K120_SametFundIdType::class,
//    ProtocolType.CREDIT_OFFER                       to K121_CreditOfferIdType::class,
//    ProtocolType.CREDIT_DEAL                        to K122_CreditDealIdType::class,
//
//    ImplementationType.GLOBAL_PROPERTY              to K200_GlobalPropertyIdType::class,
//    ImplementationType.DYNAMIC_GLOBAL_PROPERTY      to K201_DynamicGlobalPropertyIdType::class,
//    ImplementationType.RESERVED                     to K202_ReservedIdType::class,
//    ImplementationType.ASSET_DYNAMIC_DATA           to K203_AssetDynamicIdType::class,
//    ImplementationType.ASSET_BITASSET_DATA          to K204_AssetBitassetIdType::class,
//    ImplementationType.ACCOUNT_BALANCE              to K205_AccountBalanceIdType::class,
//    ImplementationType.ACCOUNT_STATISTICS           to K206_AccountStatisticsIdType::class,
//    ImplementationType.TRANSACTION_HISTORY          to K207_TransactionHistoryIdType::class,
//    ImplementationType.BLOCK_SUMMARY                to K208_BlockSummaryIdType::class,
//    ImplementationType.ACCOUNT_TRANSACTION_HISTORY  to K209_AccountTransactionHistoryIdType::class,
//    ImplementationType.BLINDED_BALANCE              to K210_BlindedBalanceIdType::class,
//    ImplementationType.CHAIN_PROPERTY               to K211_ChainPropertyIdType::class,
//    ImplementationType.WITNESS_SCHEDULE             to K212_WitnessScheduleIdType::class,
//    ImplementationType.BUDGET_RECORD                to K213_BudgetRecordIdType::class,
//    ImplementationType.SPECIAL_AUTHORITY            to K214_SpecialAuthorityIdType::class,
//    ImplementationType.BUYBACK                      to K215_BuybackIdType::class,
//    ImplementationType.FBA_ACCUMULATOR              to K216_FbaAccumulatorIdType::class,
//    ImplementationType.COLLATERAL_BID               to K217_CollateralBidIdType::class,
//    ImplementationType.CREDIT_DEAL_SUMMARY          to K218_CreditDealSummaryIdType::class,



val idTypes: Map<KClass<out AbstractType>, AbstractIdType> =
    mapOf(
        K102_AccountType::class to K102_AccountIdType(),
    ) + mapOf(
        K102_AccountIdType::class to K102_AccountIdType(),
    )

inline fun <reified K: AbstractType> emptyIdType(): K = idTypes[K::class] as K

val components: Map<KClass<out GrapheneComponent>, GrapheneComponent> =
    mapOf(
        Authority::class        to Authority(),
        AccountOptions::class   to AccountOptions(),
        PublicKeyType::class       to PublicKeyType(),
        PrivateKeyType::class      to PrivateKeyType(),
        AssetOptions::class     to AssetOptions(),
    )

inline fun <reified K: GrapheneComponent> emptyComponent(): K = components[K::class] as K

fun emptyString(): String = EMPTY_SPACE

private const val GRAPHENE_ID_SEPARATOR = "."

val String.isValidGrapheneId: Boolean
    get() = matches(Regex("[0-9]+\\$GRAPHENE_ID_SEPARATOR[0-9]+\\$GRAPHENE_ID_SEPARATOR[0-9]+")) &&
            split(GRAPHENE_ID_SEPARATOR).let {
                GRAPHENE_ID_TO_SPACE[it[0].toUInt8OrNull() ?: return@let false] ?: return@let false
                GRAPHENE_ID_TO_PROTOCOL_TYPE[it[1].toUInt8OrNull() ?: return@let false] ?: return@let false
                it[0].toUInt64OrNull() ?: return@let false
                true
            }

fun String.toGrapheneSpace(): ObjectSpace {
    if (!isValidGrapheneId) throw IllegalArgumentException("Invalid graphene id!")
    val uid = split(GRAPHENE_ID_SEPARATOR)[0].toUInt8()
    return GRAPHENE_ID_TO_SPACE[uid] ?: throw IllegalArgumentException("Invalid graphene id!")
}

fun String.toGrapheneType(): ObjectType {
    if (!isValidGrapheneId) throw IllegalArgumentException("Invalid graphene id!")
    val uid = split(GRAPHENE_ID_SEPARATOR)[1].toUInt8()
    return GRAPHENE_SPACE_TO_TYPE[toGrapheneSpace()]?.get(uid) ?: throw IllegalArgumentException("Invalid graphene id!")
}

fun String.toGrapheneInstance(): ObjectInstance {
    if (!isValidGrapheneId) throw IllegalArgumentException("Invalid graphene id!")
    val uid = split(GRAPHENE_ID_SEPARATOR)[2].toUInt64()
    return uid
}

fun <T: AbstractIdType> String.toGrapheneObjectId(): T {
    logloglog()
    return GRAPHENE_TYPE_TO_ID_CONSTRUCTOR[toGrapheneType()]!!.call(toGrapheneInstance()) as T
}

val AbstractType.standardId: String
    get() = "${id.space}$GRAPHENE_ID_SEPARATOR${id.type}$GRAPHENE_ID_SEPARATOR${id.instance}"


