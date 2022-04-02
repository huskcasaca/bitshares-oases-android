package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K216_FbaAccumulatorObject(
    @SerialName("id")
    override val id: K216_FbaAccumulatorIdType,
    @SerialName("accumulated_fba_fees")
    val accumulatedFbaFees: share_type,
    @SerialName("designated_asset")
    val designatedAsset: Optional<K103_AssetType> = optional(),
) : AbstractObject(), K216_FbaAccumulatorType {

//    bool is_configured( const database& db )const;
}