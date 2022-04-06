package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import bitshareskit.models.PriceFeed
import bitshareskit.models.SimplePrice
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.util.*


@Entity(tableName = AssetBitassetData.TABLE_NAME)
data class AssetBitassetData(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    /*{
        "id":"2.4.13",
        "asset_id":"1.3.113",
        "feeds":[],
        "current_feed":{
            "settlement_price":{
                "base":{
                    "amount":11,
                    "asset_id":"1.3.113"
                },
                "quote":{
                    "amount":500,
                    "asset_id":"1.3.0"
                }
            },
            "maintenance_collateral_ratio":1600,
            "maximum_short_squeeze_ratio":1010,
            "core_exchange_rate":{
                "base":{
                    "amount":26400000,
                    "asset_id":"1.3.113"
                },
                "quote":{
                    "amount":1000000000,
                    "asset_id":"1.3.0"
                }
            },
            "initial_collateral_ratio":1600
        },
        "current_feed_publication_time":"2020-07-08T23:35:15",
        "current_maintenance_collateralization":{
            "base":{
                "amount":800,
                "asset_id":"1.3.0"
            },
            "quote":{
                "amount":11,
                "asset_id":"1.3.113"
            }
        },
        "current_initial_collateralization":{
            "base":{
                "amount":800,
                "asset_id":"1.3.0"
            },
            "quote":{
                "amount":11,
                "asset_id":"1.3.113"
            }
        },
        "options":{
            "feed_lifetime_sec":86400,
            "minimum_feeds":7,
            "force_settlement_delay_sec":86400,
            "force_settlement_offset_percent":200,
            "maximum_force_settlement_volume":50,
            "short_backing_asset":"1.3.0",
            "extensions":{
                "maximum_short_squeeze_ratio": 1020,
                "margin_call_fee_ratio": 10,
                "force_settle_fee_percent": 100
            }
        },
        "force_settled_volume":0,
        "is_prediction_market":false,
        "settlement_price":{
            "base":{
                "amount":0,
                "asset_id":"1.3.0"
            },
            "quote":{
                "amount":0,
                "asset_id":"1.3.0"
            }
        },
        "settlement_fund":0,
        "asset_cer_updated":false,
        "feed_cer_updated":false
    }*/

    companion object {
        @Ignore const val TABLE_NAME = "asset_bitasset_data"

        @Ignore const val KEY_ASSET_ID = "asset_id"
        @Ignore const val KEY_FEEDS = "feeds"
        @Ignore const val KEY_CURRENT_FEED = "current_feed"

        @Ignore const val KEY_SETTLEMENT_PRICE = "settlement_price"

        @Ignore const val KEY_MAINTENANCE_COLLATERAL_RATIO = "maintenance_collateral_ratio"
        @Ignore const val KEY_MAXIMUM_SHORT_SQUEEZE_RATIO = "maximum_short_squeeze_ratio"
        @Ignore const val KEY_CORE_EXCHANGE_RATE = "core_exchange_rate"
        @Ignore const val KEY_INITIAL_COLLATERAL_RATIO = "initial_collateral_ratio"

        @Ignore const val KEY_CURRENT_FEED_PUBLICATION_TIME = "current_feed_publication_time"
        @Ignore const val KEY_CURRENT_MAINTENANCE_COLLATERALIZATION = "current_maintenance_collateralization"

        @Ignore const val KEY_OPTIONS = "options"
        @Ignore const val KEY_FEED_LIFETIME_SEC = "feed_lifetime_sec"
        @Ignore const val KEY_MINIMUM_FEEDS = "minimum_feeds"
        @Ignore const val KEY_FORCE_SETTLEMENT_DELAY_SEC = "force_settlement_delay_sec"
        @Ignore const val KEY_FORCE_SETTLEMENT_OFFSET_PERCENT = "force_settlement_offset_percent"
        @Ignore const val KEY_MAXIMUM_FORCE_SETTLEMENT_VOLUME = "maximum_force_settlement_volume"
        @Ignore const val KEY_SHORT_BACKING_ASSET = "short_backing_asset"
        @Ignore const val KEY_EXTENSIONS = "extensions"

        @Ignore const val KEY_FORCE_SETTLED_VOLUME = "force_settled_volume"
        @Ignore const val KEY_IS_PREDICTION_MARKET = "is_prediction_market"
        @Ignore const val KEY_SETTLEMENT_FUND = "settlement_fund"
        @Ignore const val KEY_ASSET_CER_UPDATED = "asset_cer_updated"
        @Ignore const val KEY_FEED_CER_UPDATED = "feed_cer_updated"

        @Ignore const val KEY_MARGIN_CALL_FEE_RATIO = "margin_call_fee_ratio"
        @Ignore const val KEY_FORCE_SETTLE_FEE_PERCENT = "force_settle_fee_percent"
    }

    @Suppress("SuspiciousVarProperty")
    @ColumnInfo(name = "asset_uid") var assetUid = ChainConfig.EMPTY_INSTANCE
        get() = asset.uid

    val asset: AssetObject get() = rawJson.optGrapheneInstance(KEY_ASSET_ID)

    @delegate:Ignore val currentFeed: PriceFeed by lazy { rawJson.optItem(KEY_CURRENT_FEED) }
    @delegate:Ignore val currentPublishTime: Date by lazy { rawJson.optGrapheneTime(KEY_CURRENT_FEED_PUBLICATION_TIME) }

    @delegate:Ignore private val options: JSONObject by lazy { rawJson.optJSONObject(KEY_OPTIONS) }
//    @delegate:Ignore val options1: AssetOptions by lazy { AssetOptions.fromJson(rawJson.optJSONObject(AccountObject.KEY_OPTIONS)) }

    @delegate:Ignore val feedLifetimeSec: Int by lazy { options.optInt(KEY_FEED_LIFETIME_SEC) }
    @delegate:Ignore val minimumFeeds: Int by lazy { options.optInt(KEY_MINIMUM_FEEDS) }
    @delegate:Ignore val forceSettlementDelaySec: Int by lazy { options.optInt(KEY_FORCE_SETTLEMENT_DELAY_SEC) }
    @delegate:Ignore val forceSettlementOffsetPercent: Int by lazy { options.optInt(KEY_FORCE_SETTLEMENT_OFFSET_PERCENT) }
    @delegate:Ignore val maximumForceSettlementVolumePercent: Int by lazy { options.optInt(KEY_MAXIMUM_FORCE_SETTLEMENT_VOLUME) }
    @delegate:Ignore val shortBackingAsset: AssetObject by lazy { options.optGrapheneInstance(KEY_SHORT_BACKING_ASSET) }
    @delegate:Ignore val forceSettledVolume: Long by lazy { rawJson.optLong(KEY_FORCE_SETTLED_VOLUME) }

    @delegate:Ignore val settlementPrice: SimplePrice by lazy { rawJson.optItem(KEY_SETTLEMENT_PRICE) }
    @delegate:Ignore val settlementFund: Long by lazy { rawJson.optLong(KEY_SETTLEMENT_FUND) }

    @delegate:Ignore val marginCallFeeRatio: UShort by lazy { options.optJSONObject(KEY_EXTENSIONS).optUShort(KEY_MARGIN_CALL_FEE_RATIO) }
    @delegate:Ignore val forceSettleFeePercent: UShort by lazy { options.optJSONObject(KEY_EXTENSIONS).optUShort(KEY_FORCE_SETTLE_FEE_PERCENT) }

    @delegate:Ignore val isPredictionMarket: Boolean by lazy { rawJson.optBoolean(KEY_IS_PREDICTION_MARKET) }

    @delegate:Ignore val feeds: List<PriceFeed> by lazy {
        rawJson.optIterable<JSONArray>(KEY_FEEDS).map {
            PriceFeed(it.optJSONArray(1).optJSONObject(1)).apply {
                provider = createGraphene(it.optString(0))
                time = formatIsoTime(it.optJSONArray(1).optString(0))
            }
        }
    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

//    "options":{
//        "feed_lifetime_sec":86400,
//        "minimum_feeds":7,
//        "force_settlement_delay_sec":86400,
//        "force_settlement_offset_percent":200,
//        "maximum_force_settlement_volume":50,
//        "short_backing_asset":"1.3.0",
//        "extensions":{
//            "maximum_short_squeeze_ratio": 1020,
//            "margin_call_fee_ratio": 10,
//            "force_settle_fee_percent": 100
//        }
//    }

    // FIXME: 2021/9/22 serialize unsigned int
    data class AssetOptions(
//        val feedLifetimeSec: UInt,
//        val minimumFeeds: UByte,
        val marginCallFeeRatio: UShort,
        val forceSettleFeePercent: UShort,
    ) {

        companion object{
            const val KEY_FEED_LIFETIME_SEC = "feed_lifetime_sec"
            const val KEY_MINIMUM_FEEDS = "minimum_feeds"
            const val KEY_FORCE_SETTLEMENT_DELAY_SEC = "force_settlement_delay_sec"
            const val KEY_FORCE_SETTLEMENT_OFFSET_PERCENT = "force_settlement_offset_percent"
            const val KEY_MAXIMUM_FORCE_SETTLEMENT_VOLUME = "maximum_force_settlement_volume"
            const val KEY_SHORT_BACKING_ASSET = "short_backing_asset"
            const val KEY_MARGIN_CALL_FEE_RATIO = "margin_call_fee_ratio"
            const val KEY_FORCE_SETTLE_FEE_PERCENT = "force_settle_fee_percent"
            const val KEY_EXTENSIONS = "extensions"
            fun fromJson(rawJson: JSONObject): AssetOptions {
                return AssetOptions(
                    rawJson.optJSONObject(KEY_EXTENSIONS).optUShort(KEY_MARGIN_CALL_FEE_RATIO),
                    rawJson.optJSONObject(KEY_EXTENSIONS).optUShort(KEY_FORCE_SETTLE_FEE_PERCENT),
                )
            }

        }
    }

}
