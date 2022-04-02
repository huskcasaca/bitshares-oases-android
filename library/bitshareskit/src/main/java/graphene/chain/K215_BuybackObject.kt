package graphene.chain

import graphene.protocol.AssetType
import graphene.protocol.BuybackIdType
import graphene.protocol.BuybackType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K215_BuybackObject(
    @SerialName("id")
    override val id: BuybackIdType,
    @SerialName("asset_to_buy")
    val assetToBuy: AssetType,
) : AbstractObject(), BuybackType
