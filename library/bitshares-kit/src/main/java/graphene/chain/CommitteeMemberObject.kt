package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class K105_CommitteeMemberObject(
    @SerialName("id")
    override val id: CommitteeMemberId,
    @SerialName("committee_member_account")
    val committeeMemberAccount: AccountIdType,
    @SerialName("vote_id")
    val voteId: VoteIdType,
    @SerialName("total_votes")
    val totalVotes: UInt64 = 0U,
    @SerialName("url")
    val url: String,
) : AbstractObject(), CommitteeMemberIdType