package bitshareskit.ks_models

import androidx.room.Ignore
import bitshareskit.ks_exntended.KAssetAmountType

data class KPriceType(
    val base: KAssetAmountType,
    val quote: KAssetAmountType
) {
    companion object {
        @Ignore const val KEY_BASE = "base"
        @Ignore const val KEY_QUOTE = "quote"
        @Ignore const val KEY_AMOUNT = "amount"
        @Ignore const val KEY_ASSET_ID = "asset_id"

    }
}





//data class KPrice(
//    val base: KAmount = emptyComponent(),
//    val quote: KAmount = emptyComponent(),
//) {
//    companion object {
//        @Ignore const val KEY_BASE = "base"
//        @Ignore const val KEY_QUOTE = "quote"
//        @Ignore const val KEY_AMOUNT = "amount"
//        @Ignore const val KEY_ASSET_ID = "asset_id"
//        val EMPTY = KPrice()
//    }
//}


