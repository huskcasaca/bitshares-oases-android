package graphene.chain

import graphene.protocol.AssetIdType
import graphene.protocol.BuybackId
import graphene.protocol.BuybackIdType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K215_BuybackObject(
    @SerialName("id")
    override val id: BuybackId,
    @SerialName("asset_to_buy")
    val assetToBuy: AssetIdType,
) : AbstractObject(), BuybackIdType
