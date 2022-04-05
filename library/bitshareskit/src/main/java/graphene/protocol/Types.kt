package graphene.protocol

import graphene.serializers.OptionalSerializer
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

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

typealias time_point_sec = @Serializable(with = TimePointSecSerializer::class) Instant
//typealias account_id_type = AccountIdType
//typealias uint64_t = UInt64
//typealias uint32_t = UInt32
//typealias uint16_t = UInt16
//typealias uint8_t = UInt8
//typealias share_type = ShareType
//typealias time_point_sec = @Serializable(with = TimePointSecSerializer::class) Instant
//typealias extensions_type = ExtensionsType
//typealias asset = Asset
//typealias memo_data = MemoData
//typealias bool = Boolean
//typealias string = String
//typealias credit_deal_id_type = CreditDealIdType
//typealias asset_id_type = AssetIdType
//typealias flat_set<V> = FlatSet<V>
//typealias flat_map<K, V> = FlatMap<K, V>
//typealias void_t = Unit

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
