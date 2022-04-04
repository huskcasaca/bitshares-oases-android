package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K212_WitnessScheduleObject(
    @SerialName("id")
    override val id: WitnessScheduleId,
    @SerialName("current_shuffled_witnesses")
    val currentShuffledWitnesses: List<WitnessIdType>, // vector< witness_id_type > current_shuffled_witnesses;
) : AbstractObject(), WitnessScheduleIdType {

}