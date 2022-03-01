package bitshareskit.ks_objects

import bitshareskit.chain.ChainConfig
import bitshareskit.ks_models.GrapheneComponent
import bitshareskit.ks_object_base.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K103AssetObject(
    @SerialName(KEY_ID) override val id: K103AssetId = emptyIdType(),
    @SerialName(KEY_SYMBOL) override val symbol: String = emptyString(),
    @SerialName(KEY_ISSUER) @Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") override val issuerId: K102AccountType = emptyIdType(),
    @SerialName(KEY_PRECISION) override val precision: UByte = 0U,
    @SerialName(KEY_OPTIONS) override val options: Options = emptyComponent(),
    @SerialName(KEY_DYNAMIC_ASSET_DATA_ID) @Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") override val dynamicDataId: K203AssetDynamicType = emptyIdType(),
//    @SerialName(KEY_BITASSET_DATA_ID) var bitassetDataId: KAssetBitassetIdType = emptyIdType(),
) : K000AbstractObject(), K103AssetType {


    companion object {
        const val TABLE_NAME = "asset_object"

        const val KEY_SYMBOL = "symbol"
        const val KEY_PRECISION = "precision"
        const val KEY_ISSUER = "issuer"
        const val KEY_OPTIONS = "options"
        const val KEY_REWARD_PERCENT = "reward_percent"

        const val KEY_DYNAMIC_ASSET_DATA_ID = "dynamic_asset_data_id"
        const val KEY_BITASSET_DATA_ID = "bitasset_data_id"

        const val CORE_ASSET_ID = ChainConfig.Asset.CORE_ASSET_ID
        const val CORE_ASSET_UID = ChainConfig.Asset.CORE_ASSET_INSTANCE
        const val CORE_ASSET_SYMBOL = ChainConfig.Asset.CORE_ASSET_SYMBOL
        const val CORE_ASSET_SYMBOL_TEST = ChainConfig.Asset.CORE_ASSET_SYMBOL_TEST
        const val CORE_ASSET_PRECISION = ChainConfig.Asset.CORE_ASSET_PRECISION

    }

    @Serializable
    data class Options(
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



}
