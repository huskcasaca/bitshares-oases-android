package graphene.protocol

import bitshareskit.extensions.info
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import java.util.*


val GRAPHENE_JSON_PLATFORM_SERIALIZER = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

class SortedSetSerializer<T: Comparable<T>>(private val elementSerializer: KSerializer<out T>) : KSerializer<SortedSet<T>> {
    override val descriptor: SerialDescriptor = setSerialDescriptor(elementSerializer.descriptor)
    override fun deserialize(decoder: Decoder): SortedSet<T> {
        decoder as JsonDecoder
        return decoder.decodeJsonElement().jsonArray.map {
            decoder.json.decodeFromJsonElement(elementSerializer, it)
        }.toSortedSet()
    }
    override fun serialize(encoder: Encoder, value: SortedSet<T>) = TODO()
}

val ObjectIdTypeDescriptor = PrimitiveSerialDescriptor("ObjectIdType", PrimitiveKind.STRING)
// id serializer
class ObjectIdTypeSerializer<T: AbstractIdType> : KSerializer<T> {
    override val descriptor: SerialDescriptor = ObjectIdTypeDescriptor
    override fun deserialize(decoder: Decoder): T = decoder.decodeString().toGrapheneObjectId()
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.standardId)
}

class SortedMapSerializer<K: Comparable<K>, V>(
    private val keySerializer: KSerializer<K>, private val valueSerializer: KSerializer<V>) : KSerializer<SortedMap<K, V>> {
    override val descriptor: SerialDescriptor = mapSerialDescriptor(keySerializer.descriptor, valueSerializer.descriptor)
    override fun deserialize(decoder: Decoder): SortedMap<K, V> {
        decoder as JsonDecoder
        return decoder.decodeJsonElement().jsonArray.associateBy(
            { decoder.json.decodeFromJsonElement(keySerializer, it.jsonArray[0]) },
            { decoder.json.decodeFromJsonElement(valueSerializer, it.jsonArray[1]) }
        ).toSortedMap()
    }

    override fun serialize(encoder: Encoder, value: SortedMap<K, V>) = TODO()

}

object VoteTypeSerializer : KSerializer<VoteIdType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Vote", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): VoteIdType = decoder.decodeString().run { VoteIdType.fromStringId(this) }
    override fun serialize(encoder: Encoder, value: VoteIdType) = encoder.encodeString(value.toString())
}



abstract class AbstractStaticVariantSerializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<StaticVariant<T>> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StaticVariant", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): StaticVariant<T> {
        decoder as JsonDecoder
        decoder.decodeJsonElement().jsonArray.let {
            return deserialize(
                decoder.json.decodeFromJsonElement(it[0]),
                decoder.json.decodeFromJsonElement(elementSerializer, it[1])
            )
        }
    }
    override fun serialize(encoder: Encoder, value: StaticVariant<T>) {
        TODO("Not yet implemented")
    }
    abstract fun deserialize(tagType: Int64, storage: T): StaticVariant<T>
}


class StaticVariantSerializer<T>(elementSerializer: KSerializer<T>) : AbstractStaticVariantSerializer<T>(elementSerializer) {
    override fun deserialize(tagType: Int64, storage: T): StaticVariant<T> {
        return object : StaticVariant<T>() {
            override val tagType: Int64 = tagType
            override val storage: T = storage
        }
    }
}



object BaseSpecialAuthoritySerializer : KSerializer<BaseSpecialAuthority> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BaseSpecialAuthority", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): BaseSpecialAuthority {
        decoder as JsonDecoder
        val element = decoder.decodeJsonElement().jsonObject
        val serializer = if (element.containsKey("asset")) TopHoldersSpecialAuthority.serializer() else NoSpecialAuthority.serializer()
        return decoder.json.decodeFromJsonElement(serializer, element)
    }

    override fun serialize(encoder: Encoder, value: BaseSpecialAuthority) {
        TODO("Not yet implemented")
    }
}

object SpecialAuthoritySerializer : AbstractStaticVariantSerializer<BaseSpecialAuthority>(BaseSpecialAuthority.serializer()) {
    override fun deserialize(tagType: Int64, storage: BaseSpecialAuthority): StaticVariant<BaseSpecialAuthority> {
        return SpecialAuthority(tagType, storage)
    }
}
