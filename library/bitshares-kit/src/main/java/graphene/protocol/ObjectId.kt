package graphene.protocol

import graphene.chain.AbstractObject
import graphene.extension.info
import kotlinx.coroutines.*
import kotlin.reflect.KClass

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
//    val ordinal: Int
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

const val INVALID_INSTANCE: ULong = 0x0000_FFFF_FFFF_FFFF_UL

const val GRAPHENE_ID_SEPARATOR: String = "."


inline fun <reified K: ObjectIdType> emptyIdType(): K {
//    return K::class.members.first { it.name == "id" }.returnType.jvmErasure.constructors.first().call(0U.toULong()) as K
    return GRAPHENE_ID_TYPE_FAST_ALOC[K::class] as K
}

//inline fun <reified K: AbstractType> emptyObjectType(): K {
//    return GRAPHENE_OBJECT_TYPE_FAST_ALOC[K::class] as K
//}

fun opsLaunch(block: () -> Unit): String {
    var times = 0
    val time = System.currentTimeMillis()

    while (System.currentTimeMillis() - time < 3000) {
        block()
        times++
    }
    "${times / 3f} ops/s".info()
    return "${times / 3f} ops/s"
}


suspend fun opsSuspend(block: () -> Unit): String {
    val time = System.currentTimeMillis()
    coroutineScope {
        (0..1000).map {
            launch(Dispatchers.IO) {
                block()
            }
        }.joinAll()
    }
    return "${1000 * 1000 / (System.currentTimeMillis() - time).toFloat()} ops/s"
}

inline fun <reified K: GrapheneComponent> emptyComponent(): K {
    return GRAPHENE_COMPONENTS_FAST_ALOC[K::class] as K
}

fun emptyString(): String = ""

val String.isValidGrapheneId: Boolean
    get() = matches(Regex("[0-9]+\\$GRAPHENE_ID_SEPARATOR[0-9]+\\$GRAPHENE_ID_SEPARATOR[0-9]+")) &&
            split(GRAPHENE_ID_SEPARATOR).let {
                GRAPHENE_SPACE_ENUM_INDEX[it[0].toUInt8OrNull() ?: return@let false] ?: return@let false
                GRAPHENE_PROTOCOL_TYPE_ENUM_INDEX[it[1].toUInt8OrNull() ?: return@let false] ?: return@let false
                it[0].toUInt64OrNull() ?: return@let false
                true
            }

fun String.toGrapheneSpace(): ObjectSpace {
//    if (!isValidGrapheneId) throw IllegalArgumentException("Invalid graphene id: $this")
    val uid = split(GRAPHENE_ID_SEPARATOR)[0].toUInt8()
    return GRAPHENE_SPACE_ENUM_INDEX[uid] ?: throw IllegalArgumentException("Invalid graphene id: $this")
}

fun String.toGrapheneType(): ObjectType {
//    if (!isValidGrapheneId) throw IllegalArgumentException("Invalid graphene id: $this")
    val uid = split(GRAPHENE_ID_SEPARATOR)[1].toUInt8()
    return GRAPHENE_SPACE_TYPE_INDEX[toGrapheneSpace()]?.get(uid) ?: throw IllegalArgumentException("Invalid graphene id: $this")
}

fun String.toGrapheneInstance(): ObjectInstance {
//    if (!isValidGrapheneId) throw IllegalArgumentException("Invalid graphene id: $this")
    val uid = split(GRAPHENE_ID_SEPARATOR)[2].toUInt64()
    return uid
}

fun ObjectType.toObjectClass(): KClass<out AbstractObject> = GRAPHENE_TYPE_TO_OBJ_CLASS[this]!!
fun ObjectType.toObjectIdClass(): KClass<out ObjectId> = GRAPHENE_TYPE_TO_IDT_CLASS[this]!!

fun <T: ObjectId> String.toGrapheneObjectId(): T {
    return GRAPHENE_TYPE_TO_IDT_CONSTRUCTOR[toGrapheneType()]!!.call(toGrapheneInstance()) as T
}



