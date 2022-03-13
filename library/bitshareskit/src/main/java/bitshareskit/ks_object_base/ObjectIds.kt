
package bitshareskit.ks_object_base

import bitshareskit.extensions.logloglog
import bitshareskit.ks_object_type.*
import bitshareskit.ks_objects.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KFunction


abstract class K000AbstractId : Cloneable, K000AbstractType {
    abstract val space: ObjectSpace
    abstract val type: ObjectType
    abstract val instance: ObjectInstance
}


// PROTOCOL_IDS
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K100NullId(
    override val space: ObjectSpace       = ObjectSpace.PROTOCOL,
    override val type: ObjectType         = ProtocolType.NULL,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K100NullType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K101BaseId(
    override val space: ObjectSpace       = ObjectSpace.PROTOCOL,
    override val type: ObjectType         = ProtocolType.BASE,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K101BaseType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K102AccountId(
    override val space: ObjectSpace       = ObjectSpace.PROTOCOL,
    override val type: ObjectType         = ProtocolType.ACCOUNT,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K102AccountType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K103AssetId(
    override val space: ObjectSpace       = ObjectSpace.PROTOCOL,
    override val type: ObjectType         = ProtocolType.ASSET,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K103AssetType




// IMPLEMENTATION_IDS
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K203AssetDynamicId(
    override val space: ObjectSpace       = ObjectSpace.IMPLEMENTATION,
    override val type: ObjectType         = ImplementationType.ASSET_DYNAMIC_DATA,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K203AssetDynamicType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K204AssetBitassetId(
    override val space: ObjectSpace       = ObjectSpace.IMPLEMENTATION,
    override val type: ObjectType         = ImplementationType.ASSET_BITASSET_DATA,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K204AssetBitassetType


val GRAPHENE_TYPE_TO_ID_CONSTRUCTOR: Map<ProtocolType, KFunction<K000AbstractId>> = mapOf(
    ProtocolType.NULL                 to K100NullId::class,
    ProtocolType.BASE                 to K101BaseId::class,
    ProtocolType.ACCOUNT              to K102AccountId::class,
    ProtocolType.ASSET                to K103AssetId::class,
    ProtocolType.FORCE_SETTLEMENT     to K102AccountId::class,
    ProtocolType.COMMITTEE_MEMBER     to K102AccountId::class,
    ProtocolType.WITNESS              to K102AccountId::class,
    ProtocolType.LIMIT_ORDER          to K102AccountId::class,
    ProtocolType.CALL_ORDER           to K102AccountId::class,
    ProtocolType.CUSTOM               to K102AccountId::class,
    ProtocolType.PROPOSAL             to K102AccountId::class,
    ProtocolType.OPERATION_HISTORY    to K102AccountId::class,
    ProtocolType.WITHDRAW_PERMISSION  to K102AccountId::class,
    ProtocolType.VESTING_BALANCE      to K102AccountId::class,
    ProtocolType.WORKER               to K102AccountId::class,
    ProtocolType.BALANCE              to K102AccountId::class,
    ProtocolType.HTLC                 to K102AccountId::class,
    ProtocolType.CUSTOM_AUTHORITY     to K102AccountId::class,
    ProtocolType.TICKET               to K102AccountId::class,
    ProtocolType.LIQUIDITY_POOL       to K102AccountId::class,
    ProtocolType.SAMET_FUND           to K102AccountId::class,
    ProtocolType.CREDIT_OFFER         to K102AccountId::class,
    ProtocolType.CREDIT_DEAL          to K102AccountId::class,
).mapValues { it.value.constructors.first() }

// id serializer
class KGrapheneIdSerializer<T: K000AbstractId> : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("KGrapheneId", PrimitiveKind.STRING)
    @Suppress("UNCHECKED_CAST")

    override fun deserialize(decoder: Decoder): T = decoder.decodeString().toGrapheneObjectId()
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.standardId)
}


private const val GRAPHENE_ID_SEPARATOR = "."

val String.isValidGrapheneId: Boolean
    get() = matches(Regex("[0-9]+\\${GRAPHENE_ID_SEPARATOR}[0-9]+\\${GRAPHENE_ID_SEPARATOR}[0-9]+")) &&
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

fun <T: K000AbstractId> String.toGrapheneObjectId(): T {
    logloglog()
    return GRAPHENE_TYPE_TO_ID_CONSTRUCTOR[toGrapheneType()]!!.call(toGrapheneSpace(),toGrapheneType(), toGrapheneInstance()) as T
}

val K000AbstractType.standardId: String
    get() = "${id.space}$GRAPHENE_ID_SEPARATOR${id.type}$GRAPHENE_ID_SEPARATOR${id.instance}"
