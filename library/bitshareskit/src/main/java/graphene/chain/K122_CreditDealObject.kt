package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K122_CreditDealObject(
    @SerialName("id")
    override val id: CreditDealId,
    @SerialName("borrower")
    val borrower: AccountIdType, //< Borrower
    @SerialName("offer_id")
    val offer: CreditOfferIdType, //< ID of the credit offer
    @SerialName("offer_owner")
    val offerOwner: AccountIdType, //< Owner of the credit offer, redundant info for ease of querying
    @SerialName("debt_asset")
    val debtAsset: AssetIdType, //< Asset type of the debt, redundant info for ease of querying
    @SerialName("debt_amount")
    val debtAmount: ShareType,  //< How much funds borrowed
    @SerialName("collateral_asset")
    val collateralAsset: AssetIdType, //< Asset type of the collateral
    @SerialName("collateral_amount")
    val collateralAmount: ShareType, //< How much funds in collateral
    @SerialName("fee_rate")
    val fee_rate: UInt32, // = 0U //< Fee rate, the demominator is GRAPHENE_FEE_RATE_DENOM
    @SerialName("latest_repay_time") @Serializable(TimePointSecSerializer::class)
    val latest_repay_time: Instant, //< The deadline when the debt should be repaid
) : AbstractObject(), CreditDealIdType {

}

