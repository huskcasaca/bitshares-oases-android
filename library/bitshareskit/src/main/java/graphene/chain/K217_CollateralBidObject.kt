package graphene.chain

import graphene.protocol.AccountIdType
import graphene.protocol.CollateralBidId
import graphene.protocol.CollateralBidIdType
import graphene.protocol.PriceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K217_CollateralBidObject(
    @SerialName("id")
    override val id: CollateralBidId,
    @SerialName("bidder")
    val bidder: AccountIdType,
    @SerialName("inv_swan_price")
    val invertedSwanPrice: PriceType, // Collateral / Debt
) : AbstractObject(), CollateralBidIdType {

//    asset get_additional_collateral()const { return inv_swan_price.base; }
//    asset get_debt_covered()const { return inv_swan_price.quote; }
//    asset_id_type debt_type()const { return inv_swan_price.quote.asset_id; }

}