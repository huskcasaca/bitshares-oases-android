package graphene.chain

import graphene.protocol.K102_AccountType
import graphene.protocol.K217_CollateralBidIdType
import graphene.protocol.K217_CollateralBidType
import graphene.protocol.PriceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K217_CollateralBidObject(
    @SerialName("id")
    override val id: K217_CollateralBidIdType,
    @SerialName("bidder")
    val bidder: K102_AccountType,
    @SerialName("inv_swan_price")
    val invSwanPrice: PriceType, // Collateral / Debt
) : AbstractObject(), K217_CollateralBidType {

//    asset get_additional_collateral()const { return inv_swan_price.base; }
//    asset get_debt_covered()const { return inv_swan_price.quote; }
//    asset_id_type debt_type()const { return inv_swan_price.quote.asset_id; }

}