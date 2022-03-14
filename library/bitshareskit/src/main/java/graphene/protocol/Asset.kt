package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssetOptions(
    @SerialName(KEY_MAX_SUPPLY) val maxSupply: UInt64 = 0U,
    @SerialName(KEY_MARKET_FEE_PERCENT) val marketFeePercent: UInt32 = 0U,
    @SerialName(KEY_MAX_MARKET_FEE) val maxMarketFee: UInt64 = 0U,
    @SerialName(KEY_ISSUER_PERMISSIONS) val issuerPermissions: UInt32 = 0x00U,
    @SerialName(KEY_FLAGS) val flags: UInt32 = 0x00U,
    @SerialName(KEY_DESCRIPTION) val description: String = emptyString(),
) : GrapheneComponent {
    companion object {
        const val KEY_MAX_SUPPLY = "max_supply"
        const val KEY_MARKET_FEE_PERCENT = "market_fee_percent"
        const val KEY_MAX_MARKET_FEE = "max_market_fee"
        const val KEY_ISSUER_PERMISSIONS = "issuer_permissions"
        const val KEY_FLAGS = "flags"
        const val KEY_CORE_EXCHANGE_RATE = "core_exchange_rate"
        const val KEY_WHITELIST_AUTHORITIES = "whitelist_authorities"
        const val KEY_BLACKLIST_AUTHORITIES = "blacklist_authorities"
        const val KEY_WHITELIST_MARKETS = "whitelist_markets"
        const val KEY_BLACKLIST_MARKETS = "blacklist_markets"
        const val KEY_DESCRIPTION = "description"
        const val KEY_MAIN = "main"
        const val KEY_MARKET = "market"
        const val KEY_EXTENSIONS = "extensions"
    }
}


data class KAssetAmountType(
    val amount: Long,
    val asset: K103_AssetType
) {
    companion object {
        private const val KEY_AMOUNT = "amount"
        private const val KEY_ASSET_ID = "asset_id"
    }
}

