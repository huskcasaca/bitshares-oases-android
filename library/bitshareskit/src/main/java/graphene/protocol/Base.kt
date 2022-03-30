package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import java.util.*

typealias AccountAuthMap = FlatMap<@Serializable(with = ObjectIdTypeSerializer::class) K102_AccountType, Weight>
typealias KeyAuthMap = FlatMap<PublicKeyType, Weight>
typealias AddressAuthMap = FlatMap<AddressType, Weight>

// threshold weight
typealias Weight = UInt16

typealias ExtensionsType = @Serializable(with = SortedSetSerializer::class) SortedSet<@Serializable(with = StaticVariantSerializer::class) FutureExtensions>

typealias FutureExtensions = StaticVariant<Unit>

typealias FlatSet<T> = @Serializable(with = SortedSetSerializer::class) SortedSet<T>
typealias FlatMap<K, V> = @Serializable(with = SortedMapSerializer::class) SortedMap<K, V>

typealias TypeSet<T> = @Serializable(with = SortedSetSerializer::class) SortedSet< T>

typealias PairArray<A, B> = @Serializable(with = PairAsArraySerializer::class) Pair<A, B>


class PairAsArraySerializer<A, B>(
    private val firstSerializer: KSerializer<A>, private val secondSerializer: KSerializer<B>
) : KSerializer<Pair<A, B>> {
    override val descriptor: SerialDescriptor = mapSerialDescriptor(firstSerializer.descriptor, secondSerializer.descriptor)
    override fun deserialize(decoder: Decoder): Pair<A, B> {
        return (decoder as JsonDecoder).decodeJsonElement().jsonArray.let {
            Pair(
                decoder.json.decodeFromJsonElement(firstSerializer, it[0]),
                decoder.json.decodeFromJsonElement(secondSerializer, it[1])
            )
        }
    }
    override fun serialize(encoder: Encoder, value: Pair<A, B>) {
        (encoder as JsonEncoder).encodeJsonElement(
            buildJsonArray {
                add(encoder.json.encodeToJsonElement(firstSerializer, value.first))
                add(encoder.json.encodeToJsonElement(secondSerializer, value.second))
            }
        )
    }
}

internal val bytesComparator = Comparator<ByteArray> { o1, o2 ->
    var i = 0
    var j = 0
    while (i < o1.size && j < o2.size) {
        val a: Int = o1[i].toInt() and 0xff
        val b: Int = o2[j].toInt() and 0xff
        if (a != b) {
            return@Comparator a - b
        }
        i++
        j++
    }
    return@Comparator o1.size - o2.size
}

interface Extension<T> : GrapheneComponent

annotation class Optional
