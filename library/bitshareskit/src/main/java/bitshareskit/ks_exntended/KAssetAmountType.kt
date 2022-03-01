package bitshareskit.ks_exntended

import bitshareskit.ks_objects.K103AssetType

data class KAssetAmountType(
    val amount: Long,
    val asset: K103AssetType
) {
    companion object {
        private const val KEY_AMOUNT = "amount"
        private const val KEY_ASSET_ID = "asset_id"
    }
}

//data class KAmount(
//    val amount: Long = 0,
//    val asset: KAsset = emptyKGrapheneObject()
//) {
//    companion object {
//        val EMPTY = KAmount()
//    }
//}

