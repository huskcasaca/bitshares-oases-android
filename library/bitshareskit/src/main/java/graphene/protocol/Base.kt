package graphene.protocol

import kotlinx.serialization.Serializable
import java.util.*


typealias AccountAuthMap = SortedMap<@Serializable(with = ObjectIdTypeSerializer::class) K102_AccountType, Weight>
typealias KeyAuthMap = SortedMap<PublicKeyType, Weight>
typealias AddressAuthMap = SortedMap<AddressType, Weight>

// threshold weight
typealias Weight = UInt16

typealias ExtensionsType = @Serializable(with = SortedSetSerializer::class) SortedSet<@Serializable(with = StaticVariantSerializer::class) FutureExtensions>

typealias FutureExtensions = StaticVariant<Unit>



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