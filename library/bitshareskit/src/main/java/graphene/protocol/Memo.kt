package graphene.protocol

import kotlinx.serialization.Serializable


@Serializable
data class MemoData(
    val from: PublicKeyType,
    val to: PublicKeyType,
    /**
     * 64 bit nonce format:
     * [  8 bits | 56 bits   ]
     * [ entropy | timestamp ]
     * Timestamp is number of microseconds since the epoch
     * Entropy is a byte taken from the hash of a new private key
     *
     * This format is not mandated or verified; it is chosen to ensure uniqueness of key-IV pairs only. This should
     * be unique with high probability as long as the generating host has a high-resolution clock OR a strong source
     * of entropy for generating private keys.
     */
    val nonce: UInt64 = 0U,
    /**
     * This field contains the AES encrypted packed @ref memo_message
     */
    val message: String, // TODO: 2022/4/5 List<Char>
) {

//    /// @note custom_nonce is for debugging only; do not set to a nonzero value in production
//    void        set_message(const fc::ecc::private_key& priv,
//    const fc::ecc::public_key& pub, const string& msg, uint64_t custom_nonce = 0);
//
//    std::string get_message(const fc::ecc::private_key& priv,
//    const fc::ecc::public_key& pub)const;
}
