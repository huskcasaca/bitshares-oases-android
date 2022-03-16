package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.modules.SerializersModule
import java.util.*

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


val GRAPHENE_JSON_PLATFORM_SERIALIZER = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}


// id serializer
class ObjectIdTypeSerializer<T: AbstractIdType> : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ObjectIdType", PrimitiveKind.STRING)

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