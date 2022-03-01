package bitshareskit.ks_objects

import bitshareskit.ks_object_base.K103AssetId
import bitshareskit.ks_object_base.K203AssetDynamicId
import bitshareskit.ks_object_base.UInt64
import bitshareskit.ks_object_base.emptyIdType
import kotlinx.serialization.SerialName

data class KAssetDynamicData(
    @SerialName(KEY_ID) override val id: K203AssetDynamicId = emptyIdType(),
    @SerialName(KEY_CURRENT_SUPPLY) override val currentSupply: UInt64,
    @SerialName(KEY_CONFIDENTIAL_SUPPLY) override val confidentialSupply: UInt64,
    @SerialName(KEY_ACCUMULATED_FEES) override val accumulatedFees: UInt64,
    @SerialName(KEY_ACCUMULATED_COLLATERAL_FEES) override val accumulatedCollateralFees: UInt64,
    @SerialName(KEY_FEE_POOL) override val feePool: UInt64,
) : K000AbstractObject(), K203AssetDynamicType {

    companion object {
        const val TABLE_NAME = "asset_dynamic_data"

        const val KEY_CURRENT_SUPPLY = "current_supply"
        const val KEY_CONFIDENTIAL_SUPPLY = "confidential_supply"
        const val KEY_ACCUMULATED_FEES = "accumulated_fees"
        const val KEY_ACCUMULATED_COLLATERAL_FEES = "accumulated_collateral_fees"
        const val KEY_FEE_POOL = "fee_pool"
    }

}
