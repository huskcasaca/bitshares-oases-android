package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class AccountOptions(
    @SerialName("memo_key")
    val memoKey: PublicKeyType = PublicKeyType(), // TODO
    @SerialName("voting_account")
    val votingAccount: K102_AccountType = emptyIdType(),
    @SerialName("num_witness")
    val witnessNumber: UInt16 = 0U,
    @SerialName("num_committee")
    val committeeNumber: UInt16 = 0U,
    @SerialName("votes")
    val vote: FlatSet<VoteIdType> = sortedSetOf(),
    @SerialName("extensions")
    val extensions: ExtensionsType = sortedSetOf(),
) : GrapheneComponent