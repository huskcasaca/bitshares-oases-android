package graphene.protocol

import graphene.serializers.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.*

// threshold weight

typealias ExtensionsType = StatSet<FutureExtensions>
typealias FutureExtensions = @Serializable(with = FutureExtensionSerializer::class) Unit

fun emptyExtension() = sortedSetOf<Unit>()

object FutureExtensionSerializer : StaticVarSerializer<Unit>(
    listOf(
        Unit::class
    )
)

typealias FlatSet<T> = @Serializable(with = SortedSetSerializer::class) SortedSet<T>
typealias StatSet<T> = @Serializable(with = SortedSetSerializer::class) SortedSet<T>

typealias FlatMap<K, V> = @Serializable(with = FlatMapSerializer::class) SortedMap<K, V>
typealias TypeSet<T> = @Serializable(with = SortedSetSerializer::class) SortedSet<T>
typealias FlatPair<A, B> = @Serializable(with = FlatPairSerializer::class) Pair<A, B>

typealias PriceFeeds = FlatMap<AccountId, FlatPair<@Serializable(TimePointSecSerializer::class) Instant, PriceFeedWithIcr>>


typealias Ripemd160 = String
typealias Sha256 = String

typealias BlockIdType = Ripemd160 //typealias block_id_type = fc::ripemd160; TODO
typealias ChecksumType = Ripemd160 //typealias checksum_type = fc::ripemd160; TODO
typealias TransactionIdType = Ripemd160 //typealias transaction_id_type = fc::ripemd160; TODO
typealias DigestType = Sha256 //typealias digest_type = fc::sha256; TODO
typealias SignatureType = String // fc::ecc::compact_signature; TODO
typealias ShareType = Int64 //typealias share_type = safe<int64_t>; TODO
typealias WeightType = UInt16 //typealias weight_type = uint16_t; TODO

// crypto
typealias BlindFactorType = Sha256 //typedef fc::sha256                               blind_factor_type;
typealias CommitmentType = String //typedef zero_initialized_array<unsigned char,33> commitment_type;
//typedef zero_initialized_array<unsigned char,33> public_key_data;
//typedef fc::sha256                               private_key_secret;
//typedef zero_initialized_array<unsigned char,65> public_key_point_data; ///< the full non-compressed version of the ECC point
//typedef zero_initialized_array<unsigned char,72> signature;
//typedef zero_initialized_array<unsigned char,65> compact_signature;
typealias RangeProofType = List<Char> //typedef std::vector<char>                        range_proof_type;
//typedef zero_initialized_array<unsigned char,78> extended_key_data;

//using private_key_type = fc::ecc::private_key;
typealias ChainIdType = Sha256  //using chain_id_type = fc::sha256;
//using ratio_type = boost::rational<int32_t>;

//typealias time_point_sec = @Serializable(with = TimePointSecSerializer::class) Instant
typealias time_point_sec = @Serializable(with = TimePointSecSerializer::class) LocalDateTime

@Serializable(with = OptionalSerializer::class)
data class Optional<T>(
    val valueSafe: T? = null
) {
    val value get() = valueSafe!!
    val isPresent get() = valueSafe != null

    override fun toString(): String {
        return valueSafe.toString()
    }
}

fun <T> optional(value: T? = null) = Optional(value)

fun <T> Optional<T>.getOrNull() = valueSafe
fun <T> Optional<T>.getOrThrow() = value
fun <T> Optional<T>.getOrElse(fallback: () -> T) = valueSafe ?: fallback()
