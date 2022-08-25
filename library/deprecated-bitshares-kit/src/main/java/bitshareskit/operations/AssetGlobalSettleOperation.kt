package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.SimplePrice
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AssetGlobalSettleOperation(
    var issuer: AccountObject,
    var asset: AssetObject,
    var price: SimplePrice
): Operation() {

    /*{
        "fee":{
            "amount":"100",
            "asset_id":"1.3.0"
        },
        "issuer":"1.2.25563",
        "asset_to_settle":"1.3.1552",
        "settle_price":{
            "base":{
                "amount":"100000",
                "asset_id":"1.3.1552"
            },
            "quote":{
                "amount":"100000",
                "asset_id":"1.3.0"
            }
        },
        "extensions":[
        ]
    }*/

    companion object {

        const val KEY_ISSUER = "issuer"
        const val KEY_ASSET_TO_SETTLE = "asset_to_settle"
        const val KEY_SETTLE_PRICE = "settle_price"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetGlobalSettleOperation {
            return AssetGlobalSettleOperation(
                rawJson.optGrapheneInstance(KEY_ISSUER),
                rawJson.optGrapheneInstance(KEY_ASSET_TO_SETTLE),
                rawJson.optItem(KEY_SETTLE_PRICE)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSET_GLOBAL_SETTLE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}