package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K203_AssetDynamicData(
    @SerialName("id")
    override val id: K203_AssetDynamicIdType = emptyIdType(),
    @SerialName("current_supply")
    override val currentSupply: ShareType,
    @SerialName("confidential_supply")
    override val confidentialSupply: ShareType,
    @SerialName("accumulated_fees")
    override val accumulatedFees: ShareType,
    @SerialName("accumulated_collateral_fees")
    override val accumulatedCollateralFees: ShareType,
    @SerialName("fee_pool")
    override val feePool: ShareType,
) : AbstractObject(), K203_AssetDynamicType