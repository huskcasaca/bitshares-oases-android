package bitshareskit.models

import bitshareskit.extensions.createAssetObject
import bitshareskit.objects.AssetObject
import org.java_json.JSONObject

data class Ticker(
    val base: AssetObject,
    val quote: AssetObject,
    val latest: Double = Double.NaN,
    val lowestAsk: Double = Double.NaN,
    val lowestAskBaseSize: Double = Double.NaN,
    val lowestAskQuoteSize: Double = Double.NaN,
    val highestBid: Double = Double.NaN,
    val highestBidBaseSize: Double = Double.NaN,
    val highestBidQuoteSize: Double = Double.NaN,
    val percentChange: Double = Double.NaN,
    val baseVolume: Double = Double.NaN,
    val quoteVolume: Double = Double.NaN,
//    val time: Date = Double.NaN,
) {
    
    companion object {

        private const val KEY_BASE = "base"
        private const val KEY_QUOTE = "quote"
        private const val KEY_LATEST = "latest"
        private const val KEY_LOWEST_ASK = "lowest_ask"
        private const val KEY_LOWEST_ASK_BASE_SIZE = "lowest_ask_base_size"
        private const val KEY_LOWEST_ASK_QUOTE_SIZE = "lowest_ask_quote_size"
        private const val KEY_HIGHEST_BID = "highest_bid"
        private const val KEY_HIGHEST_BID_BASE_SIZE = "highest_bid_base_size"
        private const val KEY_HIGHEST_BID_QUOTE_SIZE = "highest_bid_quote_size"
        private const val KEY_PERCENT_CHANGE= "percent_change"
        private const val KEY_BASE_VOLUME = "base_volume"
        private const val KEY_QUOTE_VOLUME = "quote_volume"
        private const val KEY_TIME = "time"

        fun fromJson(rawJson: JSONObject, base: Long, quote: Long): Ticker {
            return Ticker(
                createAssetObject(base, rawJson.optString(KEY_BASE)),
                createAssetObject(quote, rawJson.optString(KEY_QUOTE)),
                rawJson.optDouble(KEY_LATEST),
                rawJson.optDouble(KEY_LOWEST_ASK),
                rawJson.optDouble(KEY_LOWEST_ASK_BASE_SIZE),
                rawJson.optDouble(KEY_LOWEST_ASK_QUOTE_SIZE),
                rawJson.optDouble(KEY_HIGHEST_BID),
                rawJson.optDouble(KEY_HIGHEST_BID_BASE_SIZE),
                rawJson.optDouble(KEY_HIGHEST_BID_QUOTE_SIZE),
                rawJson.optDouble(KEY_PERCENT_CHANGE),
                rawJson.optDouble(KEY_BASE_VOLUME),
                rawJson.optDouble(KEY_QUOTE_VOLUME),
//                rawJson.optGrapheneTime(KEY_TIME),
            )
        }
        fun fromJson(rawJson: JSONObject, base: AssetObject, quote: AssetObject): Ticker {
            return Ticker(
                base,
                quote,
                rawJson.optDouble(KEY_LATEST),
                rawJson.optDouble(KEY_LOWEST_ASK),
                rawJson.optDouble(KEY_LOWEST_ASK_BASE_SIZE),
                rawJson.optDouble(KEY_LOWEST_ASK_QUOTE_SIZE),
                rawJson.optDouble(KEY_HIGHEST_BID),
                rawJson.optDouble(KEY_HIGHEST_BID_BASE_SIZE),
                rawJson.optDouble(KEY_HIGHEST_BID_QUOTE_SIZE),
                rawJson.optDouble(KEY_PERCENT_CHANGE),
                rawJson.optDouble(KEY_BASE_VOLUME),
                rawJson.optDouble(KEY_QUOTE_VOLUME),
//                rawJson.optGrapheneTime(KEY_TIME),
            )
        }



        val EMPTY = Ticker(
            AssetObject.EMPTY,
            AssetObject.EMPTY
        )

    }

    val isValid get() = !latest.isNaN()

    val market get() = Market(base, quote)

    /*{
        "id": 1141,
        "result": {
            "time": "2020-08-24T12:47:03",
            "base": "BTS",
            "quote": "XBTSX.STH",
            "latest": "0.02631578947368421",
            "lowest_ask": "0.02631578947368421",
            "lowest_ask_base_size": "495.57029",
            "lowest_ask_quote_size": "18831.671020",
            "highest_bid": "0.024",
            "highest_bid_base_size": "0.00251",
            "highest_bid_quote_size": "0.104583",
            "percent_change": "-5.26",
            "base_volume": "15591.81773",
            "quote_volume": "597309.500045"
        }
    }*/


}