package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class AccountOptions(
    @SerialName(KEY_MEMO_KEY)
    val memoKey: PublicKeyType = PublicKeyType.EMPTY,
    @SerialName(KEY_VOTING_ACCOUNT) @Serializable(with = ObjectIdTypeSerializer::class)
    val votingAccount: K102_AccountIdType = emptyIdType(),
    @SerialName(KEY_NUM_WITNESS)
    val witnessNumber: UInt16 = 0U,
    @SerialName(KEY_NUM_COMMITTEE)
    val committeeNumber: UInt16 = 0U,
    @SerialName(KEY_VOTES)
    val vote: Set<VoteType> = sortedSetOf(),
    @SerialName(KEY_EXTENSIONS)
    val extensions: Set<Extensions> = emptySet(),
) : GrapheneComponent {

    companion object {
        const val KEY_MEMO_KEY = "memo_key"
        const val KEY_VOTING_ACCOUNT = "voting_account"
        const val KEY_NUM_WITNESS = "num_witness"
        const val KEY_NUM_COMMITTEE = "num_committee"
        const val KEY_VOTES = "votes"
        const val KEY_EXTENSIONS = "extensions"
    }

}