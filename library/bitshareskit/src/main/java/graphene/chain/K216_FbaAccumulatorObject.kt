package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K216_FbaAccumulatorObject(
    @SerialName("id")
    override val id: FbaAccumulatorIdType,
    @SerialName("accumulated_fba_fees")
    val accumulatedFbaFees: share_type,
    @SerialName("designated_asset")
    val designatedAsset: Optional<AssetType> = optional(),
) : AbstractObject(), FbaAccumulatorType {

//    bool is_configured( const database& db )const;
}