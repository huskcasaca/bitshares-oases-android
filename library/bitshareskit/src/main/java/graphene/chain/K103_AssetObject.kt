package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K103_AssetObject(
    @SerialName("id")
    override val id: AssetId,

    @SerialName("symbol")
    override val symbol: String,
    @SerialName("issuer")
    override val issuer: AccountIdType,
    @SerialName("precision")
    override val precision: uint8_t,
    @SerialName("options")
    override val options: AssetOptions,

    @SerialName("dynamic_asset_data_id")
    override val dynamicData: AssetDynamicDataIdType,
    @SerialName("bitasset_data_id")
    override val bitassetData: Optional<AssetBitassetDataIdType> = optional(),
    @SerialName("buyback_account")
    override val buybackAccount: Optional<AccountIdType> = optional(),
    @SerialName("for_liquidity_pool")
    override val liquidityPool: Optional<LiquidityPoolIdType> = optional(),

    ) : AbstractObject(), AssetIdType
