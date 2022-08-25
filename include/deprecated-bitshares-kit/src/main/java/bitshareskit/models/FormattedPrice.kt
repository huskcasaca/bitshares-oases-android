package bitshareskit.models

import androidx.room.Ignore
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.formatAssetBigDecimal
import bitshareskit.extensions.formatNumber
import bitshareskit.extensions.putSerializable
import bitshareskit.objects.JsonSerializable
import org.java_json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

data class FormattedPrice(
    override val base: AssetAmount,
    override val quote: AssetAmount,
    val factor: BigDecimal = BigDecimal.ONE
): JsonSerializable, Price {

    companion object {
        @Ignore const val KEY_BASE = "base"
        @Ignore const val KEY_QUOTE = "quote"
        @Ignore const val KEY_AMOUNT = "amount"
        @Ignore const val KEY_ASSET_ID = "asset_id"

        fun fromJson(rawJson: JSONObject): FormattedPrice {
            val base = AssetAmount.fromJson(rawJson.optJSONObject(KEY_BASE))
            val quote =  AssetAmount.fromJson(rawJson.optJSONObject(KEY_QUOTE))
            return FormattedPrice(base, quote)
        }
    }

    @Ignore override var isInverted = false

    override val value
        get() = if (formatAssetBigDecimal(quote).multiply(factor).compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else formatAssetBigDecimal(base).divide(formatAssetBigDecimal(quote).multiply(factor), base.asset.precision, RoundingMode.HALF_EVEN)

    override val valueInverted
        get() =  if (formatAssetBigDecimal(base).compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else formatAssetBigDecimal(quote).multiply(factor).divide(formatAssetBigDecimal(base), quote.asset.precision, RoundingMode.HALF_EVEN)

    // FIXME: 2021/9/3 incorrect real value
    override val realValue
        get() = if (formatAssetBigDecimal(quote).multiply(factor).compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else formatAssetBigDecimal(base).divide(formatAssetBigDecimal(quote).multiply(factor), base.asset.precision, RoundingMode.HALF_EVEN)

    override val realValueInverted
        get() = if (formatAssetBigDecimal(base).compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else formatAssetBigDecimal(quote).multiply(factor).divide(formatAssetBigDecimal(base), quote.asset.precision, RoundingMode.HALF_EVEN)

    override val isValid get() = !(base.amount == 0L && quote.amount == 0L && base.asset.uid == ChainConfig.GLOBAL_INSTANCE && quote.asset.uid == ChainConfig.GLOBAL_INSTANCE)


    override val invertedPair
        get() = FormattedPrice(quote, base)

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