package bitshareskit.models

import androidx.room.ColumnInfo
import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.createGrapheneEmptyInstance
import bitshareskit.objects.AccountObject
import bitshareskit.objects.GrapheneSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONObject
import java.util.*

data class PriceFeed(@ColumnInfo(name = "data") val rawJson: JSONObject): GrapheneSerializable {

    /*{
        "settlement_price":{
            "base":{
                "amount":11,
                "asset_id":"1.3.113"
            },
            "quote":{
                "amount":500,
                "asset_id":"1.3.0"
            }
        },
        "maintenance_collateral_ratio":1600,
        "maximum_short_squeeze_ratio":1010,
        "core_exchange_rate":{
            "base":{
                "amount":33,
                "asset_id":"1.3.113"
            },
            "quote":{
                "amount":1250,
                "asset_id":"1.3.0"
            }
        },
        "initial_collateral_ratio":1600
    }*/

    companion object {

        const val KEY_SETTLEMENT_PRICE = "settlement_price"

        const val KEY_MAINTENANCE_COLLATERAL_RATIO = "maintenance_collateral_ratio"
        const val KEY_MAXIMUM_SHORT_SQUEEZE_RATIO = "maximum_short_squeeze_ratio"
        const val KEY_CORE_EXCHANGE_RATE = "core_exchange_rate"
        const val KEY_INITIAL_COLLATERAL_RATIO = "initial_collateral_ratio"

        fun fromJson(rawJson: JSONObject): PriceFeed = PriceFeed(rawJson)
    }

    val settlementPrice: SimplePrice
    val coreExchangeRate: SimplePrice // cer
    val maintenanceCollateralRatio: Int // mcr
    val maximumShortSqueezeRatio: Int // mssr
    val initialCollateralRatio: Int // icr
    var time: Date = Date()
    var provider: AccountObject = createGrapheneEmptyInstance()


    init {
        settlementPrice = SimplePrice.fromJson(rawJson.optJSONObject(KEY_SETTLEMENT_PRICE))
        coreExchangeRate = SimplePrice.fromJson(rawJson.optJSONObject(KEY_CORE_EXCHANGE_RATE))
        maintenanceCollateralRatio = rawJson.optInt(KEY_MAINTENANCE_COLLATERAL_RATIO)
        maximumShortSqueezeRatio = rawJson.optInt(KEY_MAXIMUM_SHORT_SQUEEZE_RATIO)
        initialCollateralRatio = rawJson.optInt(KEY_INITIAL_COLLATERAL_RATIO)
    }

    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
    }

}