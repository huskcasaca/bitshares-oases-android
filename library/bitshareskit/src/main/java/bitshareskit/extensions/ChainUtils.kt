package bitshareskit.extensions

import android.net.Uri
import bitshareskit.chain.ChainConfig
import bitshareskit.models.AssetAmount
import bitshareskit.models.Price
import bitshareskit.objects.AssetObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.Instant
import java.util.*
import kotlin.math.pow


fun formatAssetBigDecimal(amount: Long, asset: AssetObject): BigDecimal = formatAssetBigDecimal(amount, asset.precision).stripTrailingZerosFixes()
fun formatAssetBigDecimal(amount: Long, precision: Int): BigDecimal = amount.toBigDecimal().divide(10.0.pow(precision).toLong().toBigDecimal()).stripTrailingZerosFixes()
fun formatAssetBigDecimal(amount: BigDecimal, asset: AssetObject): BigDecimal = formatAssetBigDecimal(formatAssetInteger(amount, asset.precision), asset.precision).stripTrailingZerosFixes()
fun formatAssetBigDecimal(amount: BigDecimal, precision: Int): BigDecimal = formatAssetBigDecimal(formatAssetInteger(amount, precision), precision).stripTrailingZerosFixes()
fun formatAssetBigDecimal(assetAmount: AssetAmount): BigDecimal = formatAssetBigDecimal(assetAmount.amount, assetAmount.asset.precision).stripTrailingZerosFixes()

//fun formatAssetInteger(amount: BigDecimal, precision: Int): Long = (amount * 10.0.pow(precision).toLong().toBigDecimal()).toLong()
fun formatAssetInteger(amount: BigDecimal, precision: Int): Long = amount.movePointRight(precision).toLong()
fun formatAssetInteger(amount: BigDecimal, asset: AssetObject): Long = amount.movePointRight(asset.precision).toLong()

fun formatAssetBalance(amount: Long, asset: AssetObject): String = "${formatAssetBigDecimal(amount, asset.precision).formatNumber()} ${asset.symbol}"
fun formatAssetBalance(amount: Long, precision: Int, symbol: String): String = "${formatAssetBigDecimal(amount, precision).formatNumber()} $symbol"
fun formatAssetBalance(assetAmount: AssetAmount): String = formatAssetBalance(assetAmount.amount, assetAmount.asset)


fun formatAssetBigDecimal(amount: Double, asset: AssetObject): BigDecimal = BigDecimal(amount).setScale(asset.precision, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()
fun formatAssetBigDecimal(amount: Double, precision: Int): BigDecimal = BigDecimal(amount).setScale(precision, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()

fun formatAssetAmount(amount: BigDecimal, asset: AssetObject) = AssetAmount(formatAssetInteger(amount, asset), asset)
fun formatAssetAmount(amount: Long, asset: AssetObject) = AssetAmount(amount, asset)
fun formatAssetAmount(amount: Double, asset: AssetObject) = formatAssetAmount(BigDecimal(amount), asset)

//fun formatTradePair(tradePair: TradePair, invert: Boolean = false): String {
//    return if (invert) {
//        formatAssetBigDecimal((formatAssetBigDecimal(tradePair.quote) / formatAssetBigDecimal(tradePair.base)), tradePair.quote.asset).formatNumber() + "${tradePair.quote.asset.symbol}/${tradePair.base.asset.symbol}"
//    } else {
//        formatAssetBigDecimal((formatAssetBigDecimal(tradePair.base) / formatAssetBigDecimal(tradePair.quote)), tradePair.base.asset).formatNumber() + "${tradePair.base.asset.symbol}/${tradePair.quote.asset.symbol}"
//    }
//}

fun formatTradePrice(amount: AssetAmount, price: Price): AssetAmount {
    // FIXME: 2021/1/25    java.lang.IllegalArgumentException: No match asset in Price
    //        at ChainUtilsKt.formatTradePrice(ChainUtils.kt:47)
    //        at com.bitshares.android.user_interface.asset.AssetViewModel$bitassetSettledFundCollateralRatio$1.invoke(AssetViewModel.kt:103)
    //        at com.bitshares.android.user_interface.asset.AssetViewModel$bitassetSettledFundCollateralRatio$1.invoke(AssetViewModel.kt:32)
    if (price.base.asset.uid != amount.asset.uid && price.quote.asset.uid != amount.asset.uid) throw IllegalArgumentException("No match asset in Price")
    return if (price.base.asset.uid == amount.asset.uid) {
        AssetAmount(BigDecimal(amount.amount).multiply(price.realValueInverted).setScale(0, RoundingMode.HALF_EVEN).longValueExact(), price.quote.asset)
    } else {
        AssetAmount(BigDecimal(amount.amount).multiply(price.realValue).setScale(0, RoundingMode.HALF_EVEN).longValueExact(), price.base.asset)
    }
}

fun formatGraphenePercentage(percentage: Int, scale: Int): String = BigDecimal(percentage / ChainConfig.Asset.GRAPHENE_1_PERCENT.toDouble()).setScale(scale, RoundingMode.HALF_EVEN).formatNumber() + "%"
fun formatGrapheneRatio(ratio: Int): String = BigDecimal(ratio / 1000.toDouble()).setScale(3, RoundingMode.HALF_EVEN).formatNumber()
fun formatGrapheneRatio(ratio: UShort): String = BigDecimal(ratio.toString()).setScale(3, RoundingMode.HALF_EVEN).movePointLeft(3).formatNumber()
fun formatGrapheneRatioBigDecimal(ratio: Int): BigDecimal = BigDecimal(ratio / 1000.toDouble()).setScale(3, RoundingMode.HALF_EVEN)

fun formatPercentage(value: Double, scale: Int): String = BigDecimal(100.0 * value).setScale(scale, RoundingMode.HALF_EVEN).formatNumber() + "%"
fun formatPercentage(value: Float, scale: Int): String = BigDecimal(100.0 * value).setScale(scale, RoundingMode.HALF_EVEN).formatNumber() + "%"
fun formatRatio(baseValue: Long, quoteValue: Long, scale: Int): String = (if (quoteValue == 0L) BigDecimal.ZERO else BigDecimal(baseValue).divide(quoteValue.toBigDecimal(), scale, RoundingMode.HALF_EVEN)).formatNumber()
//fun formatPercentage(value: BigDecimal, scale: Int): String = value.setScale(scale, RoundingMode.HALF_EVEN).formatNumber() + "%"
fun formatPercentage(baseValue: Long, quoteValue: Long, scale: Int): String = (if (quoteValue == 0L) BigDecimal.ZERO else BigDecimal(100).multiply(BigDecimal(baseValue)).divide(quoteValue.toBigDecimal(), scale, RoundingMode.HALF_EVEN)).formatNumber() + "%"
//fun formatGrapheneRatio(ratio: Number): String = BigDecimal(ratio.toDouble()).setScale(3, RoundingMode.HALF_EVEN).formatNumber()



fun BigDecimal.formatNumber(): String {
    val formatter = NumberFormat.getNumberInstance()
    formatter.maximumFractionDigits = scale()
    return formatter.format(this).toString()
}

fun BigDecimal.isZero(): Boolean = compareTo(BigDecimal.ZERO) == 0

fun BigDecimal.isNotZero(): Boolean = compareTo(BigDecimal.ZERO) != 0


fun formatIsoTime(time: String): Date {
    runCatching { Date.from(Instant.parse( "${time}Z" )) }.onSuccess {
        return it
    }
    return Date.from(Instant.parse( "1970-01-01T00:00:00Z" ))
}

// TODO: replace with String.toUri()
fun parseUrl(url: String): Uri {
    return Uri.parse(url)
}


fun Int.toBitMaskBoolean(mask: Int): Boolean {
    return (this and mask) != 0
}

fun Int.toBooleanList(size: Int = Int.SIZE_BITS): List<Boolean> {
    return List(size.coerceAtMost(Int.SIZE_BITS)) {
        this shr it and 1 == 1
    }
}


const val EMPTY_UID = 0L
const val GLOBAL_UID = 1L

