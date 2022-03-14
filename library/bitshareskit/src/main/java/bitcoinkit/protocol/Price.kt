package graphene.protocol

import androidx.room.Ignore

data class Price(
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

