package bitshareskit.models

import androidx.room.Ignore
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.formatAssetBigDecimal
import bitshareskit.extensions.formatNumber
import bitshareskit.extensions.putSerializable
import bitshareskit.objects.GrapheneSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.max

data class SimplePrice(
    override val base: AssetAmount,
    override val quote: AssetAmount
): GrapheneSerializable, Price {

    companion object {
        @Ignore const val KEY_BASE = "base"
        @Ignore const val KEY_QUOTE = "quote"
        @Ignore const val KEY_AMOUNT = "amount"
        @Ignore const val KEY_ASSET_ID = "asset_id"

        fun fromJson(rawJson: JSONObject): SimplePrice {
            val base = AssetAmount.fromJson(rawJson.optJSONObject(KEY_BASE))
            val quote =  AssetAmount.fromJson(rawJson.optJSONObject(KEY_QUOTE))
            return SimplePrice(base, quote)
        }
    }

    @Ignore override var isInverted = false

    val scale = max(base.asset.precision + quote.asset.precision, 8)

    override val value
        get() = if (formatAssetBigDecimal(quote).compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else formatAssetBigDecimal(base).divide(formatAssetBigDecimal(quote), scale, RoundingMode.HALF_EVEN)

    override val valueInverted
        get() =  if (formatAssetBigDecimal(base).compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else formatAssetBigDecimal(quote).divide(formatAssetBigDecimal(base), scale, RoundingMode.HALF_EVEN)

    override val realValue
        get() = if (formatAssetBigDecimal(quote).compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else BigDecimal.valueOf((base.amount * quote.asset.satoshi) / (quote.amount * base.asset.satoshi)).setScale(scale, RoundingMode.HALF_EVEN)

    override val realValueInverted
        get() = if (formatAssetBigDecimal(base).compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else BigDecimal.valueOf((quote.amount * base.asset.satoshi) / (base.amount * quote.asset.satoshi)).setScale(scale, RoundingMode.HALF_EVEN)

    override val invertedPair
        get() = SimplePrice(quote, base)

    override val isValid get() = !(base.amount == 0L && quote.amount == 0L && base.asset.uid == ChainConfig.GLOBAL_INSTANCE && quote.asset.uid == ChainConfig.GLOBAL_INSTANCE)

    val values: String get() {
        if (!isValid) return "-"
        return if (isInverted) valueInverted.formatNumber() else value.formatNumber()
    }

    val realValues: String get() {
        if (!isValid) return "-"
        return if (isInverted) realValueInverted.formatNumber() else realValue.formatNumber()
    }

    val symbols: String get() {
        return if (isInverted) "${quote.asset.symbol}/${base.asset.symbol}" else "${base.asset.symbol}/${quote.asset.symbol}"
    }

    val unit: String get() {
        return if (isInverted) quote.asset.symbol else base.asset.symbol
    }

    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject {
        return buildJsonObject {
            putSerializable(KEY_BASE, base)
            putSerializable(KEY_QUOTE, quote)
        }
    }

    override fun toString(): String {
        if (!isValid) return "-"
        return if (isInverted) "${valueInverted.formatNumber()} ${quote.asset.symbol}/${base.asset.symbol}" else "${value.formatNumber()} ${base.asset.symbol}/${quote.asset.symbol}"
    }

}

fun SimplePrice.toFormattedPrice(factor: BigDecimal = BigDecimal.ONE): FormattedPrice{
    return FormattedPrice(base, quote, factor)
}