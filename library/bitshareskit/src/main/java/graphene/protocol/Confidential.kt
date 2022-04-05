package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *  @ingroup stealth
 *  This data is encrypted and stored in the
 *  encrypted memo portion of the blind output.
 */
@Serializable
data class BlindMemo(
    @SerialName("from") val from: AccountIdType,
    @SerialName("amount") val amount: ShareType,
    @SerialName("message") val message: String,
    /** set to the first 4 bytes of the shared secret
     * used to encrypt the memo.  Used to verify that
     * decryption was successful.
     */
    @SerialName("check") val check: UInt32, // = 0
)


/**
 *  @ingroup stealth
 */
@Serializable
data class BlindInput(
    @SerialName("commitment") val commitment: CommitmentType,
    /** provided to maintain the invariant that all authority
     * required by an operation is explicit in the operation.  Must
     * match blinded_balance_id->owner
     */
    @SerialName("owner") val owner: Authority,
)


/**
 *  When sending a stealth tranfer we assume users are unable to scan
 *  the full blockchain; therefore, payments require confirmation data
 *  to be passed out of band.   We assume this out-of-band channel is
 *  not secure and therefore the contents of the confirmation must be
 *  encrypted.
 */
@Serializable
data class StealthConfirmation(
    @SerialName("one_time_key") val oneTimeKey: PublicKeyType,
    @SerialName("to") val to: Optional<PublicKeyType>,
    @SerialName("encrypted_memo") val encryptedMemo: List<Char>,
) {
    @Serializable
    internal data class MemoData(
        @SerialName("from") val from: Optional<PublicKeyType>,
        @SerialName("amount") val amount: Asset,
        @SerialName("blinding_factor") val blindingFactor: Sha256,
        @SerialName("commitment") val commitment: CommitmentType,
        @SerialName("check") val check: UInt32, // = 0
    )
}

/**
 *  @class blind_output
 *  @brief Defines data required to create a new blind commitment
 *  @ingroup stealth
 *
 *  The blinded output that must be proven to be greater than 0
 */
@Serializable
data class BlindOutput(
    @SerialName("commitment") val commitment: CommitmentType,
    /** only required if there is more than one blind output  */
    @SerialName("range_proof") val rangeProof: RangeProofType,
    @SerialName("owner") val owner: Authority,
    @SerialName("stealth_memo") val stealthMemo: Optional<StealthConfirmation>,
)