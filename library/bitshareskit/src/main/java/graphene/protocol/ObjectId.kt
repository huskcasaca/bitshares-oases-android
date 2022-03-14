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
    /* 2.2.x  */ RESERVED0                   (2U), // unused
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

@JvmInline
value class ObjectInstance(
    val id: UInt64
) {
    companion object {
        val INVALID_ID = ObjectInstance(UInt64.MAX_VALUE)
    }
}

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

val GRAPHENE_TYPE_TO_ID_CONSTRUCTOR: Map<ObjectType, KFunction<AbstractIdType>> = mapOf(
    ProtocolType.NULL to K100_NullIdType::class,
    ProtocolType.BASE to K101_BaseIdType::class,
    ProtocolType.ACCOUNT to K102_AccountIdType::class,
    ProtocolType.ASSET to K103_AssetIdType::class,
    ProtocolType.FORCE_SETTLEMENT to AbstractIdType::class,
    ProtocolType.COMMITTEE_MEMBER to AbstractIdType::class,
    ProtocolType.WITNESS to AbstractIdType::class,
    ProtocolType.LIMIT_ORDER to AbstractIdType::class,
    ProtocolType.CALL_ORDER to AbstractIdType::class,
    ProtocolType.CUSTOM to AbstractIdType::class,
    ProtocolType.PROPOSAL to AbstractIdType::class,
    ProtocolType.OPERATION_HISTORY to AbstractIdType::class,
    ProtocolType.WITHDRAW_PERMISSION to AbstractIdType::class,
    ProtocolType.VESTING_BALANCE to AbstractIdType::class,
    ProtocolType.WORKER to AbstractIdType::class,
    ProtocolType.BALANCE to AbstractIdType::class,
    ProtocolType.HTLC to AbstractIdType::class,
    ProtocolType.CUSTOM_AUTHORITY to AbstractIdType::class,
    ProtocolType.TICKET to AbstractIdType::class,
    ProtocolType.LIQUIDITY_POOL to AbstractIdType::class,
    ProtocolType.SAMET_FUND to AbstractIdType::class,
    ProtocolType.CREDIT_OFFER to AbstractIdType::class,
    ProtocolType.CREDIT_DEAL to AbstractIdType::class,

    ImplementationType.GLOBAL_PROPERTY to AbstractIdType::class,
    ImplementationType.DYNAMIC_GLOBAL_PROPERTY to AbstractIdType::class,
    ImplementationType.RESERVED0 to AbstractIdType::class,
    ImplementationType.ASSET_DYNAMIC_DATA to AbstractIdType::class,
    ImplementationType.ASSET_BITASSET_DATA to AbstractIdType::class,
    ImplementationType.ACCOUNT_BALANCE to AbstractIdType::class,
    ImplementationType.ACCOUNT_STATISTICS to AbstractIdType::class,
    ImplementationType.TRANSACTION_HISTORY to AbstractIdType::class,
    ImplementationType.BLOCK_SUMMARY to AbstractIdType::class,
    ImplementationType.ACCOUNT_TRANSACTION_HISTORY to AbstractIdType::class,
    ImplementationType.BLINDED_BALANCE to AbstractIdType::class,
    ImplementationType.CHAIN_PROPERTY to AbstractIdType::class,
    ImplementationType.WITNESS_SCHEDULE to AbstractIdType::class,
    ImplementationType.BUDGET_RECORD to AbstractIdType::class,
    ImplementationType.SPECIAL_AUTHORITY to AbstractIdType::class,
    ImplementationType.BUYBACK to AbstractIdType::class,
    ImplementationType.FBA_ACCUMULATOR to AbstractIdType::class,
    ImplementationType.COLLATERAL_BID to AbstractIdType::class,
    ImplementationType.CREDIT_DEAL_SUMMARY to AbstractIdType::class,

    ).mapValues { it.value.constructors.first() }



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
        KPublicKey::class       to KPublicKey(),
        KPrivateKey::class      to KPrivateKey(),
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
    return GRAPHENE_ID_TO_PROTOCOL_TYPE[uid] ?: throw IllegalArgumentException("Invalid graphene id!")
}

fun String.toGrapheneInstance(): ObjectInstance {
    if (!isValidGrapheneId) throw IllegalArgumentException("Invalid graphene id!")
    val uid = split(GRAPHENE_ID_SEPARATOR)[2].toUInt64()
    return ObjectInstance(uid)
}

fun <T: AbstractIdType> String.toGrapheneObjectId(): T {
    logloglog()
    return GRAPHENE_TYPE_TO_ID_CONSTRUCTOR[toGrapheneType()]!!.call(toGrapheneSpace(),toGrapheneType(), toGrapheneInstance()) as T
}

val AbstractType.standardId: String
    get() = "${id.space}$GRAPHENE_ID_SEPARATOR${id.type}$GRAPHENE_ID_SEPARATOR${id.instance}"


