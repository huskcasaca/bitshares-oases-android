package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class AccountOptions(
    @SerialName("memo_key")
    val memoKey: PublicKeyType, // TODO
    @SerialName("voting_account")
    val votingAccount: AccountIdType, // = GRAPHENE_PROXY_TO_SELF_ACCOUNT
    @SerialName("num_witness")
    val witnessNumber: UInt16, // = 0U
    @SerialName("num_committee")
    val committeeNumber: UInt16, // = 0U
    @SerialName("votes")
    val vote: FlatSet<VoteIdType>,
    @SerialName("extensions")
    val extensions: ExtensionsType,
) : GrapheneComponent {

    companion object {
        internal val INVALID = AccountOptions(
            PublicKeyType(), // val memoKey: PublicKeyType, // TODO
            emptyIdType(), // val votingAccount: K102_AccountType,
            UInt16.MAX_VALUE, // val witnessNumber: UInt16,
            UInt16.MAX_VALUE, // val committeeNumber: UInt16,
            sortedSetOf(), // val vote: FlatSet<VoteIdType>,
            sortedSetOf(), // val extensions: ExtensionsType,
        )
    }

}

