package graphene.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import java.util.*
import kotlin.Comparator

class SortedSetSerializer<T: Any>(
    private val elementSerializer: KSerializer<T>
) : KSerializer<SortedSet<T>> {
    override val descriptor: SerialDescriptor = setSerialDescriptor(elementSerializer.descriptor)
    override fun deserialize(decoder: Decoder): SortedSet<T> {
        return decoder.decodeStructure(descriptor) {
            val set = if (elementSerializer is StaticVarSerializer) {
                sortedSetOf(Comparator { o1: T, o2: T ->
                    elementSerializer.typelist.indexOf(o1::class) - elementSerializer.typelist.indexOf(o2::class)
                })
            } else sortedSetOf<T>()
            while (true) {
                val index = decodeElementIndex(descriptor)
                if (index == DECODE_DONE) break
                set.add(decodeSerializableElement(elementSerializer.descriptor, index, elementSerializer))
            }
            set
        }
    }
    override fun serialize(encoder: Encoder, value: SortedSet<T>) {
        val composite = encoder.beginCollection(elementSerializer.descriptor, value.size)
        val iterator = value.iterator()
        for (index in 0 until value.size)
            composite.encodeSerializableElement(descriptor, index, elementSerializer, iterator.next())
        composite.endStructure(descriptor)
    }
}

class SortedMapSerializer<K: Comparable<K>, V>(
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>
) : KSerializer<SortedMap<K, V>> {
    override val descriptor: SerialDescriptor = mapSerialDescriptor(keySerializer.descriptor, valueSerializer.descriptor)
    override fun deserialize(decoder: Decoder): SortedMap<K, V> {
        decoder as JsonDecoder // TODO: 2022/4/5
        return decoder.decodeJsonElement().jsonArray.associateBy(
            { decoder.json.decodeFromJsonElement(keySerializer, it.jsonArray[0]) },
            { decoder.json.decodeFromJsonElement(valueSerializer, it.jsonArray[1]) }
        ).toSortedMap()
    }
    override fun serialize(encoder: Encoder, value: SortedMap<K, V>) {
        val size = value.size
        encoder.encodeCollection(descriptor, size) {
            val iterator = value.iterator()
            var index = 0
            iterator.forEach { (k, v) ->
                encodeSerializableElement(descriptor, index++, keySerializer, k)
                encodeSerializableElement(descriptor, index++, valueSerializer, v)
            }
        }
    }
}

internal fun pairAsArraySerialDescriptor(
    firstDescriptor: SerialDescriptor,
    secondDescriptor: SerialDescriptor
) = PairAsArraySerialDescriptor(firstDescriptor, secondDescriptor)

class PairAsArraySerialDescriptor(
    val firstDescriptor: SerialDescriptor,
    val secondDescriptor: SerialDescriptor
) : SerialDescriptor {
    override val serialName: String get() = "PairAsArray"
    override val kind: SerialKind get() = StructureKind.LIST
    override val elementsCount: Int = 2
    override fun getElementName(index: Int): String = index.toString()
    override fun getElementIndex(name: String): Int =
        name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid map index")
    override fun isElementOptional(index: Int): Boolean {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return false
    }
    override fun getElementAnnotations(index: Int): List<Annotation> {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return emptyList()
    }
    override fun getElementDescriptor(index: Int): SerialDescriptor {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return when (index) {
            0 -> firstDescriptor
            1 -> secondDescriptor
            else -> error("Unreached")
        }
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PairAsArraySerialDescriptor) return false
        if (serialName != other.serialName) return false
        if (firstDescriptor != other.firstDescriptor) return false
        if (secondDescriptor != other.secondDescriptor) return false
        return true
    }
    override fun hashCode(): Int {
        var result = serialName.hashCode()
        result = 31 * result + firstDescriptor.hashCode()
        result = 31 * result + secondDescriptor.hashCode()
        return result
    }
    override fun toString(): String = "$serialName($firstDescriptor, $secondDescriptor)"
}

class PairAsArraySerializer<A, B>(
    private val firstSerializer: KSerializer<A>, private val secondSerializer: KSerializer<B>
) : KSerializer<Pair<A, B>> {
    override val descriptor: SerialDescriptor = pairAsArraySerialDescriptor(firstSerializer.descriptor, secondSerializer.descriptor)
    override fun deserialize(decoder: Decoder): Pair<A, B> {
        return decoder.decodeStructure(descriptor) {
            decodeSerializableElement(firstSerializer.descriptor, 0, firstSerializer) to
            decodeSerializableElement(secondSerializer.descriptor, 1, secondSerializer)
        }
    }
    override fun serialize(encoder: Encoder, value: Pair<A, B>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(firstSerializer.descriptor, 0, firstSerializer, value.first)
            encodeSerializableElement(secondSerializer.descriptor, 1, secondSerializer, value.second)
        }
    }
}