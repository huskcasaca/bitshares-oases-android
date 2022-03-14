package graphene.chain

import graphene.protocol.K204AssetBitassetId
import graphene.protocol.K204AssetBitassetType
import graphene.protocol.emptyIdType
import graphene.protocol.*
import kotlinx.serialization.SerialName

data class K204AssetBitassetData(
    @SerialName(KEY_ID) override val id: K204AssetBitassetId = emptyIdType(),
) : K000AbstractObject(), K204AssetBitassetType {

    companion object {

        const val TABLE_NAME = "asset_bitasset_data"

        const val KEY_ASSET_ID = "asset_id"
        const val KEY_FEEDS = "feeds"
        const val KEY_CURRENT_FEED = "current_feed"

        const val KEY_SETTLEMENT_PRICE = "settlement_price"

        const val KEY_MAINTENANCE_COLLATERAL_RATIO = "maintenance_collateral_ratio"
        const val KEY_MAXIMUM_SHORT_SQUEEZE_RATIO = "maximum_short_squeeze_ratio"
        const val KEY_CORE_EXCHANGE_RATE = "core_exchange_rate"
        const val KEY_INITIAL_COLLATERAL_RATIO = "initial_collateral_ratio"

        const val KEY_CURRENT_FEED_PUBLICATION_TIME = "current_feed_publication_time"
        const val KEY_CURRENT_MAINTENANCE_COLLATERALIZATION = "current_maintenance_collateralization"

        const val KEY_OPTIONS = "options"
        const val KEY_FEED_LIFETIME_SEC = "feed_lifetime_sec"
        const val KEY_MINIMUM_FEEDS = "minimum_feeds"
        const val KEY_FORCE_SETTLEMENT_DELAY_SEC = "force_settlement_delay_sec"
        const val KEY_FORCE_SETTLEMENT_OFFSET_PERCENT = "force_settlement_offset_percent"
        const val KEY_MAXIMUM_FORCE_SETTLEMENT_VOLUME = "maximum_force_settlement_volume"
        const val KEY_SHORT_BACKING_ASSET = "short_backing_asset"
        const val KEY_EXTENSIONS = "extensions"

        const val KEY_FORCE_SETTLED_VOLUME = "force_settled_volume"
        const val KEY_IS_PREDICTION_MARKET = "is_prediction_market"
        const val KEY_SETTLEMENT_FUND = "settlement_fund"
        const val KEY_ASSET_CER_UPDATED = "asset_cer_updated"
        const val KEY_FEED_CER_UPDATED = "feed_cer_updated"

        const val KEY_MARGIN_CALL_FEE_RATIO = "margin_call_fee_ratio"
        const val KEY_FORCE_SETTLE_FEE_PERCENT = "force_settle_fee_percent"
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
