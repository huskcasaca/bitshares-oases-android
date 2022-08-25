package bitshareskit.models

import bitshareskit.extensions.*
import bitshareskit.objects.AccountBalanceObject
import bitshareskit.objects.AssetObject
import bitshareskit.objects.GrapheneSerializable
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import kotlinx.io.core.writeLongLittleEndian
import org.java_json.JSONObject
import kotlin.math.roundToLong

data class AssetAmount(
    var amount: Long,
    var asset: AssetObject
): GrapheneSerializable {

    companion object {
        private const val KEY_AMOUNT = "amount"
        private const val KEY_ASSET_ID = "asset_id"

        // TODO: 2021/9/26 breaking changes -1L
        val EMPTY = AssetAmount(-1L, AssetObject.EMPTY)
        val EMPTY_TEST = AssetAmount(0L, AssetObject.EMPTY)

        fun fromJson(rawJson: JSONObject): AssetAmount {
            val amount = rawJson.optLong(KEY_AMOUNT)
            val asset = createGraphene<AssetObject>(rawJson.optString(KEY_ASSET_ID))
            return AssetAmount(amount, asset)
        }

        fun fromAccountBalance(accountBalance: AccountBalanceObject): AssetAmount{
            return AssetAmount(accountBalance.balance, accountBalance.asset)
        }
    }

    override fun toByteArray(): ByteArray {
        return buildPacket {
            writeLongLittleEndian(amount)
            writeSerializable(asset)
        }.readBytes()
    }

    override fun toJsonElement(): JSONObject {
        return buildJsonObject {
            putItem(KEY_AMOUNT, amount)
            putSerializable(KEY_ASSET_ID, asset)
        }
    }

    override fun toString(): String = formatAssetBalance(this)

    operator fun minus(assetAmount: AssetAmount?): AssetAmount = if (assetAmount == null || asset.uid != assetAmount.asset.uid) this else AssetAmount(amount - assetAmount.amount, asset)
    operator fun minus(value: Long): AssetAmount = AssetAmount(amount - value, asset)
    operator fun minus(value: Int): AssetAmount = AssetAmount(amount - value, asset)

    operator fun plus(assetAmount: AssetAmount?): AssetAmount = if (assetAmount == null || asset.uid != assetAmount.asset.uid) this else AssetAmount(amount + assetAmount.amount, asset)
    operator fun plus(value: Long): AssetAmount = AssetAmount(amount + value, asset)
    operator fun plus(value: Int): AssetAmount = AssetAmount(amount + value, asset)

    operator fun times(times: Long): AssetAmount = AssetAmount(amount * times, asset)
    operator fun times(times: Int): AssetAmount = AssetAmount(amount * times, asset)
    operator fun times(times: Double): AssetAmount = AssetAmount((amount * times).roundToLong(), asset)

    operator fun div(value: AssetAmount) = if (asset.uid == value.asset.uid && value.amount != 0L) 1.0 * amount / value.amount else 0.0
    operator fun div(value: Long) = if (value != 0L) 1.0 * amount / value else 0.0

    operator fun unaryMinus() = AssetAmount(- amount, asset)
    operator fun unaryPlus() = AssetAmount(+ amount, asset)

    val formattedValue get() = formatAssetBigDecimal(amount, asset.precision)

    val values get() = formattedValue.formatNumber()

    val symbols get() = asset.symbol

}