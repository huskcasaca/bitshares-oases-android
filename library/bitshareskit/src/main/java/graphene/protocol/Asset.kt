package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssetOptions(
    @SerialName(KEY_MAX_SUPPLY)
    val maxSupply: ShareType = 0U,
    @SerialName(KEY_MARKET_FEE_PERCENT)
    val marketFeePercent: UInt16 = 0U,
    @SerialName(KEY_MAX_MARKET_FEE)
    val maxMarketFee: ShareType = 0U,
    @SerialName(KEY_ISSUER_PERMISSIONS)
    val issuerPermissions: UInt16 = 0x00U,
    @SerialName(KEY_FLAGS)
    val flags: UInt16 = 0x00U,

    @SerialName(KEY_CORE_EXCHANGE_RATE)
    val coreExchangeRate: PriceType = PriceType(),

    @SerialName(KEY_WHITELIST_AUTHORITIES)
    val whitelistAuthorities: FlatSet<K102_AccountType> = sortedSetOf(),
    @SerialName(KEY_BLACKLIST_AUTHORITIES)
    val blacklistAuthorities: FlatSet<K102_AccountType> = sortedSetOf(),
    @SerialName(KEY_WHITELIST_MARKETS)
    val whitelistMarkets: FlatSet<K103_AssetType> = sortedSetOf(),
    @SerialName(KEY_BLACKLIST_MARKETS)
    val blacklistMarkets: FlatSet<K103_AssetType> = sortedSetOf(),

    @SerialName(KEY_DESCRIPTION)
    val description: String = emptyString(),

    @SerialName(KEY_EXTENSIONS)
    val extensions: AdditionalAssetOptions = AdditionalAssetOptions()
) : GrapheneComponent {
    companion object {

        private const val KEY_MAX_SUPPLY = "max_supply"
        private const val KEY_MARKET_FEE_PERCENT = "market_fee_percent"
        private const val KEY_MAX_MARKET_FEE = "max_market_fee"
        private const val KEY_ISSUER_PERMISSIONS = "issuer_permissions"
        private const val KEY_FLAGS = "flags"
        private const val KEY_CORE_EXCHANGE_RATE = "core_exchange_rate"
        private const val KEY_WHITELIST_AUTHORITIES = "whitelist_authorities"
        private const val KEY_BLACKLIST_AUTHORITIES = "blacklist_authorities"
        private const val KEY_WHITELIST_MARKETS = "whitelist_markets"
        private const val KEY_BLACKLIST_MARKETS = "blacklist_markets"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_EXTENSIONS = "extensions"

//        uint16_t get_enabled_issuer_permissions_mask() const;
//        price core_exchange_rate = price(asset(), asset(0, asset_id_type(1)));

    }
}

@Serializable
data class AssetType(
    @SerialName(KEY_AMOUNT)
    val amount: ShareType = 0U,
    @SerialName(KEY_ASSET_ID) @Serializable(with = ObjectIdTypeSerializer::class)
    val asset: K103_AssetType = emptyIdType()
) {
    companion object {
        private const val KEY_AMOUNT = "amount"
        private const val KEY_ASSET_ID = "asset_id"
    }
}



@Serializable
data class PriceType(
    @SerialName(KEY_BASE)
    val base: AssetType = AssetType(),
    @SerialName(KEY_QUOTE)
    val quote: AssetType = AssetType()
) {
    companion object {
        private const val KEY_BASE = "base"
        private const val KEY_QUOTE = "quote"

    }
}

@Serializable
data class AdditionalAssetOptions(
    @SerialName(KEY_REWARD_PERCENT)
    val rewardPercent: UInt16 = 0U, // optional
    @SerialName(KEY_WHITELIST_MARKET_FEE_SHARING)
    val whitelistMarketFeeSharing: FlatSet<K102_AccountType> = sortedSetOf(), // optional
    @SerialName(KEY_TAKER_FEE_PERCENT)
    val takerFeePercent: UInt16 = 0U, // optional
) : Extension<AdditionalAssetOptions> {

    companion object {

        private const val KEY_REWARD_PERCENT = "reward_percent"
        private const val KEY_WHITELIST_MARKET_FEE_SHARING = "whitelist_market_fee_sharing"
        private const val KEY_TAKER_FEE_PERCENT = "taker_fee_percent"

    }


}

