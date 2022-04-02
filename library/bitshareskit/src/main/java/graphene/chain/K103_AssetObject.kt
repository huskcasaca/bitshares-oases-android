package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class K103_AssetObject(
    @SerialName("id")
    override val id: AssetIdType,

    @SerialName("symbol")
    override val symbol: String,
    @SerialName("issuer")
    override val issuer: AccountType,
    @SerialName("precision")
    override val precision: uint8_t,
    @SerialName("options")
    override val options: AssetOptions,

    @SerialName("dynamic_asset_data_id")
    override val dynamicData: AssetDynamicDataType,
    @SerialName("bitasset_data_id")
    override val bitassetData: Optional<AssetBitassetDataType> = optional(),
    @SerialName("buyback_account")
    override val buybackAccount: Optional<AccountType> = optional(),
    @SerialName("for_liquidity_pool")
    override val liquidityPool: Optional<LiquidityPoolType> = optional(),

    ) : AbstractObject(), AssetType
