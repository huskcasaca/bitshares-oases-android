package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BuybackAccountOptions(
    /**
     * The asset to buy.
     */
    @SerialName("asset_to_buy")
    val assetToBuy: AssetIdType,
    /**
     * Issuer of the asset.  Must sign the transaction, must match issuer
     * of specified asset.
     */
    @SerialName("asset_to_buy_issuer")
    val assetToBuyIssuer: AccountIdType,
    /**
     * What assets the account is willing to buy with.
     * Other assets will just sit there since the account has null authority.
     */
    @SerialName("markets")
    val markets: FlatSet<AssetIdType>,
)