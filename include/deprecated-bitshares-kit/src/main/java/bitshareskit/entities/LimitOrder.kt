package bitshareskit.entities

import bitshareskit.extensions.filledPercent
import bitshareskit.models.AssetAmount
import bitshareskit.models.Market
import bitshareskit.models.SimplePrice
import bitshareskit.objects.LimitOrderObject

data class LimitOrder(
    val base: AssetAmount,
    val quote: AssetAmount,
    val order: LimitOrderObject,
    val market: Market
) {

    val price = SimplePrice(base, quote).apply { isInverted = market.base.uid != base.asset.uid }

    val isBuy get() = market.base.uid == base.asset.uid
    val isSell get() = market.base.uid == quote.asset.uid

    val totalSales = if (isSell) base else quote
    val totalReceives = if (isBuy) base else quote

    val sales  = (if (isSell) base else quote) * order.filledPercent

}

