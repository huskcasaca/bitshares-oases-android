package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class AccountOptions(
    @SerialName(KEY_MEMO_KEY)
    val memoKey: PublicKeyType = PublicKeyType(), // TODO
    @SerialName(KEY_VOTING_ACCOUNT)
    val votingAccount: K102_AccountType = emptyIdType(),
    @SerialName(KEY_NUM_WITNESS)
    val witnessNumber: UInt16 = 0U,
    @SerialName(KEY_NUM_COMMITTEE)
    val committeeNumber: UInt16 = 0U,
    @SerialName(KEY_VOTES)
    val vote: FlatSet<VoteIdType> = sortedSetOf(),
    @SerialName(KEY_EXTENSIONS)
    val extensions: ExtensionsType = sortedSetOf(),
) : GrapheneComponent {

    companion object {
        private const val KEY_MEMO_KEY = "memo_key"
        private const val KEY_VOTING_ACCOUNT = "voting_account"
        private const val KEY_NUM_WITNESS = "num_witness"
        private const val KEY_NUM_COMMITTEE = "num_committee"
        private const val KEY_VOTES = "votes"
        private const val KEY_EXTENSIONS = "extensions"
    }

}