package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AssetReserveOperation(
    var account: AccountObject,
    var amount: AssetAmount
): Operation() {

    /*{
        "fee":{
            "amount":"100",
            "asset_id":"1.3.0"
        },
        "payer":"1.2.25563",
        "amount_to_reserve":{
            "amount":"100000",
            "asset_id":"1.3.0"
        },
        "extensions":[
        ]
    }*/

    companion object {

        const val KEY_PAYER = "payer"
        const val KEY_AMOUNT_TO_RESERVE = "amount_to_reserve"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetReserveOperation {
            return AssetReserveOperation(
                rawJson.optGrapheneInstance(KEY_PAYER),
                rawJson.optItem(KEY_AMOUNT_TO_RESERVE)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSET_RESERVE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}