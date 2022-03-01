
package bitshareskit.ks_object_base

import bitshareskit.ks_object_base.K000AbstractId.Companion.standardId
import bitshareskit.ks_object_base.K000AbstractId.Companion.toGrapheneInstance
import bitshareskit.ks_object_base.K000AbstractId.Companion.toGrapheneSpace
import bitshareskit.ks_object_base.K000AbstractId.Companion.toGrapheneSpaceType
import bitshareskit.ks_object_base.K000AbstractId.Companion.toGrapheneType
import bitshareskit.ks_objects.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

// PROTOCOL_SPACE
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K100NullId(
    override val space: UInt8 = KObjectSpaceType.NULL_OBJECT.space,
    override val type: UInt8 = KObjectSpaceType.NULL_OBJECT.type,
    override val instance: UInt64 = ObjectInstance.INVALID_ID
) : K000AbstractId(), K100NullType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K102AccountId(
    override val space: UInt8 = KObjectSpaceType.ACCOUNT_OBJECT.space,
    override val type: UInt8 = KObjectSpaceType.ACCOUNT_OBJECT.type,
    override val instance: UInt64 = ObjectInstance.INVALID_ID
) : K000AbstractId(), K102AccountType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K103AssetId(
    override val space: UInt8 = KObjectSpaceType.ASSET_OBJECT.space,
    override val type: UInt8 = KObjectSpaceType.ASSET_OBJECT.type,
    override val instance: UInt64 = ObjectInstance.INVALID_ID
) : K000AbstractId(), K103AssetType


// IMPLEMENTATION_SPACE
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K203AssetDynamicId(
    override val space: UInt8 = KObjectSpaceType.ASSET_DYNAMIC_DATA.space,
    override val type: UInt8 = KObjectSpaceType.ASSET_DYNAMIC_DATA.type,
    override val instance: UInt64 = ObjectInstance.INVALID_ID
) : K000AbstractId(), K203AssetDynamicType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K204AssetBitassetId(
    override val space: UInt8 = KObjectSpaceType.ASSET_BITASSET_DATA.space,
    override val type: UInt8 = KObjectSpaceType.ASSET_BITASSET_DATA.type,
    override val instance: UInt64 = ObjectInstance.INVALID_ID
) : K000AbstractId(), K204AssetBitassetType






private val idMap: Map<KObjectSpaceType, KClass<out K000AbstractId>> = mapOf(
    KObjectSpaceType.ACCOUNT_OBJECT to K102AccountId::class,
    KObjectSpaceType.ASSET_OBJECT to K103AssetId::class
)

fun <T: K000AbstractId> KObjectSpaceType.toObjectIdClass() = idMap.getValue(this) as KClass<T>

// id serializer
class KGrapheneIdSerializer<T: K000AbstractId> : KSerializer<T> {
    companion object {
        fun <T: K000AbstractId> String.toObjectId(): T =
//        toGrapheneSpaceType().toObjectIdClass<T>().constructors.first().call(toGrapheneSpace(), toGrapheneType(), toGrapheneInstance())
            when (toGrapheneSpaceType()) {
                KObjectSpaceType.ACCOUNT_OBJECT -> K102AccountId(toGrapheneSpace(), toGrapheneType(), toGrapheneInstance())
                KObjectSpaceType.ASSET_OBJECT -> K103AssetId(toGrapheneSpace(), toGrapheneType(), toGrapheneInstance())
                else -> throw IllegalArgumentException()
            } as T
    }
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("KGrapheneId", PrimitiveKind.STRING)
    @Suppress("UNCHECKED_CAST")

    override fun deserialize(decoder: Decoder): T = decoder.decodeString().toObjectId()
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.standardId)
}

