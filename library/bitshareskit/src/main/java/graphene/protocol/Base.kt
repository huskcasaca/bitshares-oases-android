package graphene.protocol

import kotlinx.serialization.Serializable
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
