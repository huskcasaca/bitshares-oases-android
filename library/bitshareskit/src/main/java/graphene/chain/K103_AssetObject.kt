package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class K103_AssetObject(
    @SerialName("id")
    override val id: K103_AssetIdType = emptyIdType(),

    @SerialName("symbol")
    override val symbol: String = emptyString(),
    @SerialName("issuer")
    override val issuer: K102_AccountType = emptyIdType(),
    @SerialName("precision")
    override val precision: UInt8 = 0U,
    @SerialName("options")
    override val options: AssetOptions = emptyComponent(),

    @SerialName("dynamic_asset_data_id")
    override val dynamicData: K203_AssetDynamicType = emptyIdType(),
    @SerialName("bitasset_data_id")
    @Optional
    override val bitassetData: K204_AssetBitassetType = emptyIdType(),
    @Optional
    @SerialName("buyback_account")
    override val buybackAccount: K102_AccountType = emptyIdType(),
    @Optional
    @SerialName("for_liquidity_pool")
    override val forLiquidityPool: K119_LiquidityPoolType = emptyIdType(),

) : AbstractObject(), K103_AssetType
