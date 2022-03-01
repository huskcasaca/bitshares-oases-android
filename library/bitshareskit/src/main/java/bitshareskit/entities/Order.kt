package bitshareskit.entities

import org.java_json.JSONObject
import java.math.BigDecimal

data class Order(
    val price: BigDecimal,
    val base: BigDecimal,
    val quote: BigDecimal,
) {

    companion object {
        const val KEY_PRICE = "price"
        const val KEY_QUOTE = "quote"
        const val KEY_BASE = "base"

        val EMPTY get() = Order(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)

        fun fromJson(rawJson: JSONObject): Order {
            return Order(
                rawJson.optBigDecimal(KEY_PRICE, BigDecimal.ZERO),
                rawJson.optBigDecimal(KEY_BASE, BigDecimal.ZERO),
                rawJson.optBigDecimal(KEY_QUOTE, BigDecimal.ZERO),
            )
        }
    }

    var isCallOrder = false



    /*{
		"price": "6.51465566314731767391",
		"quote": "767.4999",
		"base": "4999.99757"
	}*/
}