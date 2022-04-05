package graphene.app

import graphene.chain.K111_OperationHistoryObject
import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class Application

@Serializable
data class VerifyRangeResult(
    @SerialName("success") val success: Boolean,
    @SerialName("min_val")val minVal: UInt64,
    @SerialName("max_val")val maxVal: UInt64,
)

@Serializable
data class VerifyRangeProofRewindResult(
    @SerialName("success") val success: Boolean,
    @SerialName("min_val") val minVal: UInt64,
    @SerialName("max_val") val maxVal: UInt64,
    @SerialName("value_out") val valueOut: UInt64,
    @SerialName("blind_out") val blindOut: BlindFactorType,
    @SerialName("message_out") val messageOut: String,
)

@Serializable
data class AccountAssetBalance(
    @SerialName("name") val name: String,
    @SerialName("account_id") val accountId: AccountIdType,
    @SerialName("amount") val amount: ShareType,
)

@Serializable
data class AssetHolders(
    @SerialName("asset_id") val assetId: AssetIdType,
    @SerialName("count") val count: Int, // = 0
)

@Serializable
data class history_operation_detail (
    @SerialName("total_count") val totalCount: UInt32, // = 0
    @SerialName("operation_history_objs") val operationHistoryObjs: List<K111_OperationHistoryObject>,
)

/**
 * @brief summary data of a group of limit orders
 */
@Serializable
data class limit_order_group(
    @SerialName("min_price") val minPrice: PriceType, ///< possible lowest price in the group
    @SerialName("max_price") val maxPrice: PriceType, ///< possible highest price in the group
    @SerialName("total_for_sale") val totalForSale: ShareType, ///< total amount of asset for sale, asset id is min_price.base.asset_id

){
//    limit_order_group( const std::pair<limit_order_group_key,limit_order_group_data>& p )
//    :  min_price( p.first.min_price ),
//    max_price( p.second.max_price ),
//    total_for_sale( p.second.total_for_sale )
}

