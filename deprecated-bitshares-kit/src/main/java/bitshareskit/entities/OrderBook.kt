package bitshareskit.entities

import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optIterable
import bitshareskit.objects.AssetObject
import org.java_json.JSONObject

// FIXME: 2022/1/25      java.lang.NullPointerException: Attempt to invoke virtual method 'java.util.List bitshareskit.entities.OrderBook.getAsks()' on a null object reference
//        at com.bitshares.android.user_interface.trading.TradingViewModel$special$$inlined$map$3.apply(Transformations.kt:89)
//        at androidx.lifecycle.Transformations$1.onChanged(Transformations.java:76)
data class OrderBook(
    val base: AssetObject,
    val quote: AssetObject,
    val bids: List<Order>,
    val asks: List<Order>
) {

    companion object {
        const val KEY_BASE = "base"
        const val KEY_QUOTE = "quote"
        const val KEY_BIDS = "bids"
        const val KEY_ASKS = "asks"

        val EMPTY get() = OrderBook(AssetObject.EMPTY, AssetObject.EMPTY, emptyList(), emptyList())

        fun fromJson(rawJson: JSONObject): OrderBook {
            return OrderBook(
                rawJson.optGrapheneInstance(KEY_BASE),
                rawJson.optGrapheneInstance(KEY_QUOTE),
                rawJson.optIterable<JSONObject>(KEY_BIDS).map { Order.fromJson(it) },
                rawJson.optIterable<JSONObject>(KEY_ASKS).map { Order.fromJson(it) },
            )
        }
    }

    /*{
		"price": "6.51465566314731767391",
		"quote": "767.4999",
		"base": "4999.99757"
	}*/
}