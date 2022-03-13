package bitshareskit.ks_object_base

import bitshareskit.ks_objects.ObjectInstance

val GRAPHENE_ID_TO_SPACE =
    ObjectSpace.values().associateBy { it.id }

val GRAPHENE_ID_TO_PROTOCOL_TYPE =
    ProtocolType.values().associateBy { it.id }

val GRAPHENE_ID_TO_IMPLEMENTATION_TYPE =
    ImplementationType.values().associateBy { it.id }

val GRAPHENE_SPACE_TO_TYPE = mapOf(
    ObjectSpace.PROTOCOL to GRAPHENE_ID_TO_PROTOCOL_TYPE,
    ObjectSpace.IMPLEMENTATION to GRAPHENE_ID_TO_IMPLEMENTATION_TYPE,
)

enum class ObjectSpace(val id: UInt8) {
    RELATIVE_PROTOCOL(0U),
    PROTOCOL(1U),
    IMPLEMENTATION(2U),
    ACCOUNT_HISTORY(4U),
    MARKET_HISTORY_SPACE(5U),
    CUSTOM_OPERATIONS(7U)
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
    /* 2.2.x  */ RESERVED_OBJECT             (2U), // unused
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
