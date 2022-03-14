
package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


abstract class K000AbstractId : Cloneable, K000AbstractType {
    abstract val space: ObjectSpace
    abstract val type: ObjectType
    abstract val instance: ObjectInstance
}

// PROTOCOL_IDS
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K100NullId(
    override val space: ObjectSpace = ObjectSpace.PROTOCOL,
    override val type: ObjectType = ProtocolType.NULL,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K100NullType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K101BaseId(
    override val space: ObjectSpace = ObjectSpace.PROTOCOL,
    override val type: ObjectType = ProtocolType.BASE,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K101BaseType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K102AccountId(
    override val space: ObjectSpace = ObjectSpace.PROTOCOL,
    override val type: ObjectType = ProtocolType.ACCOUNT,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K102AccountType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K103AssetId(
    override val space: ObjectSpace = ObjectSpace.PROTOCOL,
    override val type: ObjectType = ProtocolType.ASSET,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K103AssetType

// IMPLEMENTATION_IDS
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K203AssetDynamicId(
    override val space: ObjectSpace = ObjectSpace.IMPLEMENTATION,
    override val type: ObjectType = ImplementationType.ASSET_DYNAMIC_DATA,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K203AssetDynamicType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K204AssetBitassetId(
    override val space: ObjectSpace = ObjectSpace.IMPLEMENTATION,
    override val type: ObjectType = ImplementationType.ASSET_BITASSET_DATA,
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : K000AbstractId(), K204AssetBitassetType


// id serializer
class KGrapheneIdSerializer<T: K000AbstractId> : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GrapheneId", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): T = decoder.decodeString().toGrapheneObjectId()
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.standardId)
}
