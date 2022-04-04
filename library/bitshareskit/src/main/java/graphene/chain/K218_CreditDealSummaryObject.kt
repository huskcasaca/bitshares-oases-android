package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K218_CreditDealSummaryObject(
    @SerialName("id")
    override val id: CreditDealSummaryId,
    @SerialName("borrower")
    val borrower: AccountIdType, // Borrower
    @SerialName("offer_id")
    val offer: CreditOfferIdType, // ID of the credit offer
    @SerialName("offer_owner")
    val offerOwner: AccountIdType, // Owner of the credit offer, redundant info for ease of querying
    @SerialName("debt_asset")
    val debtAsset: AssetIdType, // Asset type of the debt, redundant info for ease of querying
    @SerialName("total_debt_amount")
    val totalDebtAmount: ShareType, // How much funds borrowed
) : AbstractObject(), CreditDealSummaryIdType {


}