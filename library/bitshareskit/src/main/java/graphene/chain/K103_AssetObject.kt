package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class K103_AssetObject(
    @SerialName("id")
    override val id: K103_AssetIdType,

    @SerialName("symbol")
    override val symbol: String,
    @SerialName("issuer")
    override val issuer: K102_AccountType,
    @SerialName("precision")
    override val precision: UInt8,
    @SerialName("options")
    override val options: AssetOptions,

    @SerialName("dynamic_asset_data_id")
    override val dynamicData: K203_AssetDynamicDataType,
    @SerialName("bitasset_data_id")
    override val bitassetData: Optional<K204_AssetBitassetDataType> = optional(),
    @SerialName("buyback_account")
    override val buybackAccount: Optional<K102_AccountType> = optional(),
    @SerialName("for_liquidity_pool")
    override val forLiquidityPool: Optional<K119_LiquidityPoolType> = optional(),

    ) : AbstractObject(), K103_AssetType
