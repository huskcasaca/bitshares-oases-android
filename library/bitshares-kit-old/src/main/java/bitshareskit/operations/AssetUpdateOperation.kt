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

data class AssetUpdateOperation(
    var issuer: AccountObject,
    var asset: AssetObject
): Operation() {

    /*{
        "fee":{
            "amount":"101",
            "asset_id":"1.3.0"
        },
        "issuer":"1.2.25563",
        "asset_to_update":"1.3.1552",
        "new_options":{
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
                    "asset_id":"1.3.1552"
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
            "description":"{\"main\":\"TESTCON\",\"market\":\"\",\"expiry\":\"2020-07-23\"}",
            "extensions":{
                "reward_percent":0,
                "whitelist_market_fee_sharing":[
                ]
            }
        },
        "extensions":[
        ]
    }*/

    companion object {


        const val KEY_ISSUER = "issuer"
        const val KEY_ASSET_TO_UPDATE = "asset_to_update"
        const val KEY_NEW_OPTIONS = "new_options"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetUpdateOperation {
            return AssetUpdateOperation(
                rawJson.optGrapheneInstance(KEY_ISSUER),
                rawJson.optGrapheneInstance(KEY_ASSET_TO_UPDATE)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSET_UPDATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}