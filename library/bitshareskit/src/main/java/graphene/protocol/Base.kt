package graphene.protocol

import graphene.serializers.*
import kotlinx.datetime.Instant
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.util.*
import kotlin.reflect.KClass

typealias AccountAuthMap = FlatMap<AccountIdType, Weight>
//typealias AccountAuthMap = FlatMap<@Serializable(with = ObjectIdSerializer::class) AccountIdType, Weight>
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

typealias PriceFeeds = FlatMap<AccountId, PairArray<Instant, PriceFeedWithIcr>>


typealias TypedFeeParameter = @Serializable(with = TypedFeeParameterSerializer::class) StaticVariant<FeeParameter>

typealias FeeParameters = FlatSet<TypedFeeParameter>


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
