package graphene.protocol

import graphene.serializers.*
import kotlinx.datetime.Instant
import kotlinx.serialization.*
import java.util.*

// threshold weight
typealias Weight = UInt16

typealias ExtensionsType = StatSet<FutureExtensions>
typealias FutureExtensions = @Serializable(with = FutureExtensionSerializer::class) Unit

object FutureExtensionSerializer : StaticVarSerializer<Unit>(
    listOf(
        Unit::class
    )
)

typealias FlatSet<T> = @Serializable(with = SortedSetSerializer::class) SortedSet<T>
typealias StatSet<T> = @Serializable(with = StaticVarSetSerializer::class) SortedSet<T>

typealias FlatMap<K, V> = @Serializable(with = SortedMapSerializer::class) SortedMap<K, V>
typealias TypeSet<T> = @Serializable(with = SortedSetSerializer::class) SortedSet< T>
typealias PairArray<A, B> = @Serializable(with = PairAsArraySerializer::class) Pair<A, B>

typealias PriceFeeds = FlatMap<AccountId, PairArray<Instant, PriceFeedWithIcr>>

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
