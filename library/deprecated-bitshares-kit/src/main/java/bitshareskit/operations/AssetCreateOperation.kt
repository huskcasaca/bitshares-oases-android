package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AssetCreateOperation(
    var issuer: AccountObject,
    val symbol: String,
    val precision: Int
): Operation() {

    /*{
        "fee":{
            "amount":"20001562",
            "asset_id":"1.3.0"
        },
        "issuer":"1.2.25563",
        "symbol":"TESTCON",
        "precision":5,
        "common_options":{
            "max_supply":"10000000000",
            "market_fee_percent":0,
            "max_market_fee":"0",
            "issuer_permissions":511,
            "flags":0,
            "core_exchange_rate":{
                "base":{
                    "amount":"100000",
                    "asset_id":"1.3.0"
                },
                "quote":{
                    "amount":"100000",
                    "asset_id":"1.3.1"
                }
            },
            "whitelist_authorities":[
            ],
            "blacklist_authorities":[
            ],
            "whitelist_markets":[
            ],
            "blacklist_markets":[
            ],
            "description":"{\"main\":\"TESTCONTESTCONTESTCON\",\"market\":\"TEST\",\"expiry\":\"2020-07-23\"}",
            "extensions":{
                "reward_percent":0,
                "whitelist_market_fee_sharing":[
                ]
            }
        },
        "bitasset_opts":{
            "feed_lifetime_sec":86400,
            "minimum_feeds":7,
            "force_settlement_delay_sec":86400,
            "force_settlement_offset_percent":100,
            "maximum_force_settlement_volume":2000,
            "short_backing_asset":"1.3.0",
            "extensions":[
            ]
        },
        "is_prediction_market":true,
        "extensions":[
        ]
    }*/

    companion object {

        const val KEY_ISSUER = "issuer"
        const val KEY_SYMBOL = "symbol"
        const val KEY_PRECISION = "precision"
        const val KEY_COMMON_OPTIONS = "common_options"
        const val KEY_BITASSET_OPTS = "bitasset_opts"
        const val KEY_IS_PREDICTION_MARKET = "is_prediction_market"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetCreateOperation {
            return AssetCreateOperation(
                rawJson.optGrapheneInstance(KEY_ISSUER),
                rawJson.optString(KEY_SYMBOL),
                rawJson.optInt(KEY_PRECISION)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
                result = rawJsonResult.optGrapheneInstance(1)
            }
        }
    }

    var result: AssetObject = createGrapheneEmptyInstance()

    override val operationType = OperationType.ASSET_UPDATE_FEED_PRODUCERS_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}