package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K203_AssetDynamicDataObject(
    @SerialName("id")
    override val id: K203_AssetDynamicDataIdType,
    @SerialName("current_supply")
    override val currentSupply: share_type,
    @SerialName("confidential_supply")
    override val confidentialSupply: share_type,
    @SerialName("accumulated_fees")
    override val accumulatedFees: share_type,
    @SerialName("accumulated_collateral_fees")
    override val accumulatedCollateralFees: share_type,
    @SerialName("fee_pool")
    override val feePool: share_type,
) : AbstractObject(), K203_AssetDynamicDataType