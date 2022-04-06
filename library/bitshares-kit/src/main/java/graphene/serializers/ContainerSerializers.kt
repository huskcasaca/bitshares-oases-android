package graphene.serializers

import graphene.protocol.FlatMap
import graphene.protocol.FlatPair
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import java.util.*
import kotlin.Comparator

internal fun flatPairSerialDescriptor(
    oddDescriptor: SerialDescriptor,
    evenDescriptor: SerialDescriptor
) = FlatPairSerialDescriptor(oddDescriptor, evenDescriptor)

internal class FlatPairSerialDescriptor(
    oddDescriptor: SerialDescriptor,
    evenDescriptor: SerialDescriptor
) : FlattenDescriptor(oddDescriptor, evenDescriptor) {
    override val serialName: String = "FlatPair"
}

abstract class FlattenDescriptor(
    val oddDescriptor: SerialDescriptor,
    val evenDescriptor: SerialDescriptor
) : SerialDescriptor {
    override val kind: SerialKind = StructureKind.LIST
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
        return when (index % 2) {
            0 -> oddDescriptor
            1 -> evenDescriptor
            else -> error("Unreached")
        }
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlattenDescriptor) return false
        if (serialName != other.serialName) return false
        if (oddDescriptor != other.oddDescriptor) return false
        if (evenDescriptor != other.evenDescriptor) return false
        return true
    }
    override fun hashCode(): Int {
        var result = serialName.hashCode()
        result = 31 * result + oddDescriptor.hashCode()
        result = 31 * result + evenDescriptor.hashCode()
        return result
    }
    override fun toString(): String = "$serialName($oddDescriptor, $evenDescriptor)"
}

class FlatPairSerializer<A, B>(
    private val firstSerializer: KSerializer<A>, private val secondSerializer: KSerializer<B>
) : KSerializer<FlatPair<A, B>> {
    override val descriptor: SerialDescriptor = flatPairSerialDescriptor(firstSerializer.descriptor, secondSerializer.descriptor)
    override fun deserialize(decoder: Decoder): FlatPair<A, B> {
        return decoder.decodeStructure(descriptor) {
            decodeSerializableElement(firstSerializer.descriptor, 0, firstSerializer) to
            decodeSerializableElement(secondSerializer.descriptor, 1, secondSerializer)
        }
    }
    override fun serialize(encoder: Encoder, value: FlatPair<A, B>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(firstSerializer.descriptor, 0, firstSerializer, value.first)
            encodeSerializableElement(secondSerializer.descriptor, 1, secondSerializer, value.second)
        }
    }
}

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

class FlatMapSerializer<K: Comparable<K>, V>(
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>
) : KSerializer<FlatMap<K, V>> {

    private val elementSerializer = FlatPairSerializer(keySerializer, valueSerializer)

    override val descriptor: SerialDescriptor = listSerialDescriptor(elementSerializer.descriptor)
    override fun deserialize(decoder: Decoder): FlatMap<K, V> {
        return decoder.decodeStructure(descriptor) {
            val map = sortedMapOf<K, V>()
            while (true) {
                val index = decodeElementIndex(descriptor)
                if (index == DECODE_DONE) break
                val value = decodeSerializableElement(elementSerializer.descriptor, index, elementSerializer)
                map.put(value.first, value.second)
            }
            map
        }
    }
    override fun serialize(encoder: Encoder, value: FlatMap<K, V>) {
        val size = value.size
        encoder.encodeCollection(descriptor, size) {
            var index = 0
            value.forEach {
                encodeSerializableElement(descriptor, index++, elementSerializer, it.toPair())
            }
        }
    }
}