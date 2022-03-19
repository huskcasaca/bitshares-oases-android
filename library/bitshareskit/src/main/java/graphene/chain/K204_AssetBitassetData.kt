package graphene.chain

import graphene.protocol.K204_AssetBitassetIdType
import graphene.protocol.K204_AssetBitassetType
import graphene.protocol.emptyIdType
import kotlinx.serialization.SerialName

data class K204_AssetBitassetData(
    @SerialName(KEY_ID) override val id: K204_AssetBitassetIdType = emptyIdType(),
) : AbstractObject(), K204_AssetBitassetType {

    companion object {
        private const val KEY_ASSET_ID = "asset_id"
        private const val KEY_FEEDS = "feeds"
        private const val KEY_CURRENT_FEED = "current_feed"

        private const val KEY_SETTLEMENT_PRICE = "settlement_price"

        private const val KEY_MAINTENANCE_COLLATERAL_RATIO = "maintenance_collateral_ratio"
        private const val KEY_MAXIMUM_SHORT_SQUEEZE_RATIO = "maximum_short_squeeze_ratio"
        private const val KEY_CORE_EXCHANGE_RATE = "core_exchange_rate"
        private const val KEY_INITIAL_COLLATERAL_RATIO = "initial_collateral_ratio"

        private const val KEY_CURRENT_FEED_PUBLICATION_TIME = "current_feed_publication_time"
        private const val KEY_CURRENT_MAINTENANCE_COLLATERALIZATION = "current_maintenance_collateralization"

        private const val KEY_OPTIONS = "options"
        private const val KEY_FEED_LIFETIME_SEC = "feed_lifetime_sec"
        private const val KEY_MINIMUM_FEEDS = "minimum_feeds"
        private const val KEY_FORCE_SETTLEMENT_DELAY_SEC = "force_settlement_delay_sec"
        private const val KEY_FORCE_SETTLEMENT_OFFSET_PERCENT = "force_settlement_offset_percent"
        private const val KEY_MAXIMUM_FORCE_SETTLEMENT_VOLUME = "maximum_force_settlement_volume"
        private const val KEY_SHORT_BACKING_ASSET = "short_backing_asset"
        private const val KEY_EXTENSIONS = "extensions"

        private const val KEY_FORCE_SETTLED_VOLUME = "force_settled_volume"
        private const val KEY_IS_PREDICTION_MARKET = "is_prediction_market"
        private const val KEY_SETTLEMENT_FUND = "settlement_fund"
        private const val KEY_ASSET_CER_UPDATED = "asset_cer_updated"
        private const val KEY_FEED_CER_UPDATED = "feed_cer_updated"

        private const val KEY_MARGIN_CALL_FEE_RATIO = "margin_call_fee_ratio"
        private const val KEY_FORCE_SETTLE_FEE_PERCENT = "force_settle_fee_percent"
    }


}
