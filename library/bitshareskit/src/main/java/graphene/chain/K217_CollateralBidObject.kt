package graphene.chain

import graphene.protocol.AccountType
import graphene.protocol.CollateralBidIdType
import graphene.protocol.CollateralBidType
import graphene.protocol.PriceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K217_CollateralBidObject(
    @SerialName("id")
    override val id: CollateralBidIdType,
    @SerialName("bidder")
    val bidder: AccountType,
    @SerialName("inv_swan_price")
    val invSwanPrice: PriceType, // Collateral / Debt
) : AbstractObject(), CollateralBidType {

//    asset get_additional_collateral()const { return inv_swan_price.base; }
//    asset get_debt_covered()const { return inv_swan_price.quote; }
//    asset_id_type debt_type()const { return inv_swan_price.quote.asset_id; }

}