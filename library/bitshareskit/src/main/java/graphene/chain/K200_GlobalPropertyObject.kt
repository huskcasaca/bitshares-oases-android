package graphene.chain

import graphene.protocol.*
import graphene.protocol.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K200_GlobalPropertyObject(
    @SerialName("id")
    override val id: K200_GlobalPropertyIdType,
    @SerialName("parameters")
    val parameters: ChainParameters,
    @SerialName("pending_parameters")
    val pending_parameters: Optional<ChainParameters> = optional(),
    @SerialName("next_available_vote_id")
    val next_available_vote_id: UInt32, // = 0
    @SerialName("active_committee_members")
    val active_committee_members: List<K105_CommitteeMemberType>, // updated once per maintenance interval
    @SerialName("active_witnesses")
    val active_witnesses: FlatSet<K106_WitnessType>, // updated once per maintenance interval // TODO
    // n.b. witness scheduling is done by witness_schedule object
) : AbstractObject(), K200_GlobalPropertyType

