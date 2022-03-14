
package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class AbstractIdType(
    val space: ObjectSpace,
    val type: ObjectType
) : Cloneable, AbstractType {
    abstract val instance: ObjectInstance
}

// PROTOCOL_IDS
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K100_NullIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.NULL), K100_NullType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K101_BaseIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.BASE), K101_BaseType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K102_AccountIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.ACCOUNT), K102_AccountType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K103_AssetIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.ASSET), K103_AssetType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K104_ForceSettlementIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.FORCE_SETTLEMENT), K104_ForceSettlementType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K105_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.COMMITTEE_MEMBER), K105_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K106_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.WITNESS), K106_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K107_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.LIMIT_ORDER), K107_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K108_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CALL_ORDER), K108_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K109_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CUSTOM), K109_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K110_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.PROPOSAL), K110_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K111_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K111_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K112_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K112_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K113_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K113_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K114_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K114_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K115_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K115_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K116_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K116_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K117_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K117_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K118_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K118_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K119_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K119_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K120_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K120_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K121_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K121_Type

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K122_IdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K122_Type




// IMPLEMENTATION_IDS
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K203AssetDynamicId(
    override val space: ObjectSpace = ObjectSpace.IMPLEMENTATION,
    override val type: ObjectType = ImplementationType.ASSET_DYNAMIC_DATA,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(), K203AssetDynamicType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K204AssetBitassetId(
    override val space: ObjectSpace = ObjectSpace.IMPLEMENTATION,
    override val type: ObjectType = ImplementationType.ASSET_BITASSET_DATA,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(), K204AssetBitassetType


// id serializer
class KGrapheneIdSerializer<T: AbstractIdType> : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GrapheneId", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): T = decoder.decodeString().toGrapheneObjectId()
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.standardId)
}
