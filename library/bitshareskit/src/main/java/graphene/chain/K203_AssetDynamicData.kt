package graphene.chain

import graphene.protocol.K203_AssetDynamicIdType
import graphene.protocol.K203_AssetDynamicType
import graphene.protocol.UInt64
import graphene.protocol.emptyIdType
import kotlinx.serialization.SerialName

data class K203_AssetDynamicData(
    @SerialName(KEY_ID) override val id: K203_AssetDynamicIdType = emptyIdType(),
    @SerialName(KEY_CURRENT_SUPPLY) override val currentSupply: UInt64,
    @SerialName(KEY_CONFIDENTIAL_SUPPLY) override val confidentialSupply: UInt64,
    @SerialName(KEY_ACCUMULATED_FEES) override val accumulatedFees: UInt64,
    @SerialName(KEY_ACCUMULATED_COLLATERAL_FEES) override val accumulatedCollateralFees: UInt64,
    @SerialName(KEY_FEE_POOL) override val feePool: UInt64,
) : K000_AbstractObject(), K203_AssetDynamicType {

    companion object {
        private const val KEY_CURRENT_SUPPLY = "current_supply"
        private const val KEY_CONFIDENTIAL_SUPPLY = "confidential_supply"
        private const val KEY_ACCUMULATED_FEES = "accumulated_fees"
        private const val KEY_ACCUMULATED_COLLATERAL_FEES = "accumulated_collateral_fees"
        private const val KEY_FEE_POOL = "fee_pool"
    }

}
