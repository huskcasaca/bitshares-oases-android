package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K216_FbaAccumulatorObject(
    @SerialName("id")
    override val id: FbaAccumulatorId,
    @SerialName("accumulated_fba_fees")
    val accumulatedFbaFees: ShareType,
    @SerialName("designated_asset")
    val designatedAsset: Optional<AssetIdType> = optional(),
) : AbstractObject(), FbaAccumulatorIdType {

//    bool is_configured( const database& db )const;
}