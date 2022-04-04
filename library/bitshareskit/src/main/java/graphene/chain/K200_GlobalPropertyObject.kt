package graphene.chain

import graphene.protocol.*
import graphene.protocol.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K200_GlobalPropertyObject(
    @SerialName("id")
    override val id: GlobalPropertyId,
    @SerialName("parameters")
    override val parameters: ChainParameters,
    @SerialName("pending_parameters")
    override val pendingParameters: Optional<ChainParameters> = optional(),
    @SerialName("next_available_vote_id")
    override val nextAvailableVoteId: UInt32, // = 0
    @SerialName("active_committee_members")
    override val activeCommitteeMembers: List<CommitteeMemberIdType>, // updated once per maintenance interval
    @SerialName("active_witnesses")
    override val activeWitnesses: FlatSet<WitnessIdType>, // updated once per maintenance interval // TODO
    // n.b. witness scheduling is done by witness_schedule object
) : AbstractObject(), GlobalPropertyIdType

