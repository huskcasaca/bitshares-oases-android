package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K212_WitnessScheduleObject(
    @SerialName("id")
    override val id: K212_WitnessScheduleIdType,
    @SerialName("current_shuffled_witnesses")
    val current_shuffled_witnesses: List<K106_WitnessType>, // vector< witness_id_type > current_shuffled_witnesses;
) : AbstractObject(), K212_WitnessScheduleType {

}