package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class K103_AssetObject(
    @SerialName(KEY_ID)
    override val id: K103_AssetIdType = emptyIdType(),

    @SerialName(KEY_SYMBOL)
    override val symbol: String = emptyString(),
    @SerialName(KEY_ISSUER)
    override val issuer: K102_AccountType = emptyIdType(),
    @SerialName(KEY_PRECISION)
    override val precision: UInt8 = 0U,
    @SerialName(KEY_OPTIONS)
    override val options: AssetOptions = emptyComponent(),

    @SerialName(KEY_DYNAMIC_ASSET_DATA_ID)
    override val dynamicData: K203_AssetDynamicType = emptyIdType(),
    @SerialName(KEY_BITASSET_DATA_ID)
    override val bitassetData: K204_AssetBitassetType = emptyIdType(), // optional
    @SerialName(KEY_BUYBACK_ACCOUNT)
    override val buybackAccount: K102_AccountType = emptyIdType(), // optional
    @SerialName(KEY_FOR_LIQUIDITY_POOL)
    override val forLiquidityPool: K119_LiquidityPoolType = emptyIdType(), // optional

) : K000_AbstractObject(), K103_AssetType {

    companion object {
        private const val KEY_SYMBOL = "symbol"
        private const val KEY_PRECISION = "precision"
        private const val KEY_ISSUER = "issuer"
        private const val KEY_OPTIONS = "options"
        private const val KEY_REWARD_PERCENT = "reward_percent"

        private const val KEY_DYNAMIC_ASSET_DATA_ID = "dynamic_asset_data_id"
        private const val KEY_BITASSET_DATA_ID = "bitasset_data_id"
        private const val KEY_BUYBACK_ACCOUNT = "buyback_account"
        private const val KEY_FOR_LIQUIDITY_POOL = "for_liquidity_pool"

    }



}
