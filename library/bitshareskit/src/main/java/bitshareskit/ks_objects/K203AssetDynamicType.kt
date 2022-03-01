package bitshareskit.ks_objects

import bitshareskit.ks_object_base.K203AssetDynamicId
import bitshareskit.ks_object_base.UInt64

interface K203AssetDynamicType: K000AbstractType {
    override val id: K203AssetDynamicId     get() = this as K203AssetDynamicId
    val currentSupply: UInt64               get() = 0U
    val confidentialSupply: UInt64          get() = 0U
    val accumulatedFees: UInt64             get() = 0U
    val accumulatedCollateralFees: UInt64   get() = 0U
    val feePool: UInt64                     get() = 0U
}