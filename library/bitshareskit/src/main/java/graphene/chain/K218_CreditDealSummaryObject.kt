package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K218_CreditDealSummaryObject(
    @SerialName("id")
    override val id: K218_CreditDealSummaryIdType,
    @SerialName("borrower")
    val borrower: K102_AccountType, // Borrower
    @SerialName("offer_id")
    val offerId: K121_CreditOfferType, // ID of the credit offer
    @SerialName("offer_owner")
    val offerOwner: K102_AccountType, // Owner of the credit offer, redundant info for ease of querying
    @SerialName("debt_asset")
    val debtAsset: K103_AssetType, // Asset type of the debt, redundant info for ease of querying
    @SerialName("total_debt_amount")
    val totalDebtAmount: share_type, // How much funds borrowed
) : AbstractObject(), K218_CreditDealSummaryType {


}