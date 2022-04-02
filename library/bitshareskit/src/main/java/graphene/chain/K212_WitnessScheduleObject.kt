package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K212_WitnessScheduleObject(
    @SerialName("id")
    override val id: WitnessScheduleIdType,
    @SerialName("current_shuffled_witnesses")
    val current_shuffled_witnesses: List<WitnessType>, // vector< witness_id_type > current_shuffled_witnesses;
) : AbstractObject(), WitnessScheduleType {

}