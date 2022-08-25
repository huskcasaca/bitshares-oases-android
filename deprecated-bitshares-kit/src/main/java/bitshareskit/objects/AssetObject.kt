package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.createGrapheneEmptyInstance
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optItem
import bitshareskit.models.SimplePrice
import kotlinx.serialization.Serializable
import org.java_json.JSONObject
import kotlin.math.pow

@Entity(tableName = AssetObject.TABLE_NAME)
@Serializable(with = GrapheneJsonSerializer::class)
data class AssetObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    /*{
        "id":"1.3.113",
        "symbol":"CNY",
        "precision":4,
        "issuer":"1.2.0",
        "options":{
            "max_supply":"1000000000000000",
            "market_fee_percent":10,
            "max_market_fee":"1000000000000000",
            "issuer_permissions":511,
            "flags":1,
            "core_exchange_rate":{
                "base":{
                    "amount":33,
                    "asset_id":"1.3.113"
                },
                "quote":{
                    "amount":1250,
                    "asset_id":"1.3.0"
                }
            },
            "whitelist_authorities":[
            ],
            "blacklist_authorities":[
            ],
            "whitelist_markets":[
            ],
            "blacklist_markets":[
            ],
            "description":"{\"main\":\"1 Chinese Yuan, 1元人民币\",\"short_name\":\"Yuan\",\"market\":\"BTS\"}",
            "extensions":{
                "reward_percent":4000
            }
        },
        "dynamic_asset_data_id":"2.3.113",
        "bitasset_data_id":"2.4.13"
    }*/

    companion object {
        @Ignore const val TABLE_NAME = "asset_object"

        @Ignore const val KEY_SYMBOL = "symbol"
        @Ignore const val KEY_PRECISION = "precision"
        @Ignore const val KEY_ISSUER = "issuer"
        @Ignore const val KEY_OPTIONS = "options"
        @Ignore const val KEY_MAX_SUPPLY = "max_supply"
        @Ignore const val KEY_MARKET_FEE_PERCENT = "market_fee_percent"
        @Ignore const val KEY_MAX_MARKET_FEE = "max_market_fee"
        @Ignore const val KEY_ISSUER_PERMISSIONS = "issuer_permissions"
        @Ignore const val KEY_FLAGS = "flags"
        @Ignore const val KEY_CORE_EXCHANGE_RATE = "core_exchange_rate"
        @Ignore const val KEY_WHITELIST_AUTHORITIES = "whitelist_authorities"
        @Ignore const val KEY_BLACKLIST_AUTHORITIES = "blacklist_authorities"
        @Ignore const val KEY_WHITELIST_MARKETS = "whitelist_markets"
        @Ignore const val KEY_BLACKLIST_MARKETS = "blacklist_markets"
        @Ignore const val KEY_DESCRIPTION = "description"
        @Ignore const val KEY_MAIN = "main"
        @Ignore const val KEY_MARKET = "market"
        @Ignore const val KEY_EXTENSIONS = "extensions"
        @Ignore const val KEY_REWARD_PERCENT = "reward_percent"

        @Ignore const val KEY_DYNAMIC_ASSET_DATA_ID = "dynamic_asset_data_id"
        @Ignore const val KEY_BITASSET_DATA_ID = "bitasset_data_id"


        @Ignore const val CORE_ASSET_ID = ChainConfig.Asset.CORE_ASSET_ID
        @Ignore const val CORE_ASSET_UID = ChainConfig.Asset.CORE_ASSET_INSTANCE
        @Ignore const val CORE_ASSET_SYMBOL = ChainConfig.Asset.CORE_ASSET_SYMBOL
        @Ignore const val CORE_ASSET_SYMBOL_TEST = ChainConfig.Asset.CORE_ASSET_SYMBOL_TEST
        @Ignore const val CORE_ASSET_PRECISION = ChainConfig.Asset.CORE_ASSET_PRECISION

        @Ignore const val SMARTCOIN_PREFIX = "BIT"

        fun getAssetType(asset: AssetObject): AssetObjectType = when {
            asset.symbol.isEmpty() -> AssetObjectType.UNDEFINED
            asset.uid == ChainConfig.GLOBAL_INSTANCE -> AssetObjectType.CORE
            !asset.bitassetData.isExist -> AssetObjectType.UIA
            asset.bitassetData.isExist && asset.bitassetData.isPredictionMarket -> AssetObjectType.PREDICTION
            asset.bitassetData.isExist && !asset.bitassetData.isPredictionMarket -> AssetObjectType.MPA
            else -> AssetObjectType.UNDEFINED
        }

        val CORE_ASSET = fromJson<AssetObject>(
            buildJsonObject {
                put(KEY_ID, CORE_ASSET_ID)
                put(KEY_SYMBOL, CORE_ASSET_SYMBOL)
                put(KEY_PRECISION, CORE_ASSET_PRECISION)
            }
        )

        val CORE_ASSET_TEST = fromJson<AssetObject>(
            buildJsonObject {
                put(KEY_ID, CORE_ASSET_ID)
                put(KEY_SYMBOL, CORE_ASSET_SYMBOL_TEST)
                put(KEY_PRECISION, CORE_ASSET_PRECISION)
            }
        )

        val EMPTY: AssetObject = createGrapheneEmptyInstance()

        fun precisionToRatio(precision: Int) = 10.0.pow(precision)
    }

    @Ignore private val options = rawJson.optJSONObject(KEY_OPTIONS)
    @Ignore private val extensions = rawJson.optJSONObject(KEY_EXTENSIONS)

    @Ignore val symbol: String
    @Ignore val precision: Int
    @Ignore val maxSupply: Long
    @Ignore val marketFeePercent: Int
    @Ignore val marketRewardPercent: Int
    @Ignore val maxMarketFee: Long
    @Ignore val issuerPermissions: Int = options.optInt(KEY_ISSUER_PERMISSIONS)
    @Ignore val flags: Int = options.optInt(KEY_FLAGS)
//    @Ignore val permissions: List<Boolean> = flags.toBooleanList(9)
//    @Ignore val base
//    @Ignore val quote
//    @Ignore val amount
//    @Ignore val asset_id
//      TODO: 2021/10/22 add market
//    @Ignore val whitelistAuthorities: List<Any>
//    @Ignore val blacklistAuthorities: List<Any>
//    @Ignore val whitelistMarkets: List<Any>
//    @Ignore val blacklistMarkets: List<Any>

    @Ignore val description: String
    @Ignore val descriptionMain: String
    @Ignore var dynamicData: AssetDynamicData
    @Ignore var bitassetData: AssetBitassetData
//    @Ignore val KEY_EXTENSIONS = "extensions"

    @Ignore val issuer: AccountObject = rawJson.optGrapheneInstance(KEY_ISSUER)
    val coreExchangeRate: SimplePrice get() = options.optItem(KEY_CORE_EXCHANGE_RATE)
    val assetType: AssetObjectType get() = getAssetType(this)
    val satoshi get() = precisionToRatio(precision)

    init {
        symbol = rawJson.optString(KEY_SYMBOL)
        precision = rawJson.optInt(KEY_PRECISION)

        maxSupply = options.optLong(KEY_MAX_SUPPLY)
        marketFeePercent = options.optInt(KEY_MARKET_FEE_PERCENT)
        maxMarketFee = options.optLong(KEY_MAX_MARKET_FEE)
        description = options.optString(KEY_DESCRIPTION)
        descriptionMain = options.optJSONObject(KEY_DESCRIPTION).optString(KEY_MAIN)

        marketRewardPercent = extensions.optInt(KEY_REWARD_PERCENT)

        dynamicData = rawJson.optGrapheneInstance(KEY_DYNAMIC_ASSET_DATA_ID)

        bitassetData = rawJson.optGrapheneInstance(KEY_BITASSET_DATA_ID)
    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
