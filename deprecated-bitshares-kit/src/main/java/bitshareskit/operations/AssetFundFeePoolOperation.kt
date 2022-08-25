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

data class AssetFundFeePoolOperation(
    var account: AccountObject,
    var asset: AssetObject,
    var fund: Long
): Operation() {

    /*{
        "fee":{
            "amount":"100",
            "asset_id":"1.3.0"
        },
        "from_account":"1.2.25563",
        "asset_id":"1.3.1553",
        "amount":"100000",
        "extensions":[
        ]
    }*/

    companion object {

        const val KEY_FROM_ACCOUNT = "from_account"
        const val KEY_ASSET_ID = "asset_id"
        const val KEY_AMOUNT = "amount"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetFundFeePoolOperation {
            return AssetFundFeePoolOperation(
                rawJson.optGrapheneInstance(KEY_FROM_ACCOUNT),
                rawJson.optGrapheneInstance(KEY_ASSET_ID),
                rawJson.optLong(KEY_AMOUNT)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSET_FUND_FEE_POOL_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}