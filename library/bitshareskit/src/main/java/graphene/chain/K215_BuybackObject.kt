package graphene.chain

import graphene.protocol.K103_AssetType
import graphene.protocol.K215_BuybackIdType
import graphene.protocol.K215_BuybackType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K215_BuybackObject(
    @SerialName("id")
    override val id: K215_BuybackIdType,
    @SerialName("asset_to_buy")
    val assetToBuy: K103_AssetType,
) : AbstractObject(), K215_BuybackType
