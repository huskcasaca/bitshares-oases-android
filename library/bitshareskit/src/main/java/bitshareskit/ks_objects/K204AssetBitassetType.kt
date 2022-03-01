package bitshareskit.ks_objects

import bitshareskit.ks_object_base.K203AssetDynamicId
import bitshareskit.ks_object_base.K204AssetBitassetId
import bitshareskit.ks_object_base.UInt64

interface K204AssetBitassetType: K000AbstractType {
    override val id: K204AssetBitassetId    get() = this as K204AssetBitassetId
    val currentSupply: UInt64               get() = 0U
    val confidentialSupply: UInt64          get() = 0U
    val accumulatedFees: UInt64             get() = 0U
    val accumulatedCollateralFees: UInt64   get() = 0U
    val feePool: UInt64                     get() = 0U
}