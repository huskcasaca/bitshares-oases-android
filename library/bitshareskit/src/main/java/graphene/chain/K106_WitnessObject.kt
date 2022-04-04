package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K106_WitnessObject(
    @SerialName("id")
    override val id: WitnessId,
    @SerialName("witness_account")
    val witnessAccount: AccountIdType,
    @SerialName("last_aslot")
    val lastAslot: UInt64 = 0U,
    @SerialName("signing_key")
    val signingKey: PublicKeyType,
    @SerialName("pay_vb")
    val payVb: Optional<VestingBalanceIdType> = optional(),
    @SerialName("vote_id")
    val voteId: VoteIdType,
    @SerialName("total_votes")
    val totalVotes: UInt64 = 0U,
    @SerialName("url")
    val url: String,
    @SerialName("total_missed")
    val totalMissed: Int64 = 0,
    @SerialName("last_confirmed_block_num")
    val lastConfirmedBlockNum: UInt32 = 0U,
) : AbstractObject(), WitnessIdType {

//    witness_object() : vote_id(vote_id_type::witness) {}

}
