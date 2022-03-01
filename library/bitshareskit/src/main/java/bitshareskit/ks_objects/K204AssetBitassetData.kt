package bitshareskit.ks_objects

import androidx.room.Ignore
import bitshareskit.ks_object_base.K204AssetBitassetId
import bitshareskit.ks_object_base.emptyIdType
import kotlinx.serialization.SerialName

data class K204AssetBitassetData(
    @SerialName(KEY_ID) override val id: K204AssetBitassetId = emptyIdType(),
) : K000AbstractObject(), K204AssetBitassetType {

    companion object {
        @Ignore
        const val TABLE_NAME = "asset_bitasset_data"

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


    // FIXME: 2021/9/22 serialize unsigned
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

        }
    }

}
