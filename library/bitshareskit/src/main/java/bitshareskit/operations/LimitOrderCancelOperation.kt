package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.objects.LimitOrderObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import bitshareskit.chain.Authority
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class LimitOrderCancelOperation(
    var account: AccountObject,
    var order: LimitOrderObject
): Operation() {

    /*{
        "fee":{
            "amount":482,
            "asset_id":"1.3.0"
        },
        "fee_paying_account":"1.2.893637",
        "order":"1.7.446081735",
        "extensions":[
        ]
    }*/

    companion object {
        const val KEY_FEE_PAYING_ACCOUNT = "fee_paying_account"
        const val KEY_ORDER = "order"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): LimitOrderCancelOperation {
            return LimitOrderCancelOperation(
                rawJson.optGrapheneInstance(KEY_FEE_PAYING_ACCOUNT),
                rawJson.optGrapheneInstance(KEY_ORDER)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }

        val EMPTY = LimitOrderCancelOperation(createGrapheneEmptyInstance(), createGrapheneEmptyInstance())

    }

    override val operationType = OperationType.LIMIT_ORDER_CANCEL_OPERATION

    override val authority: Authority = Authority.ACTIVE

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeSerializable(account)
        writeSerializable(order)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putSerializable(KEY_FEE_PAYING_ACCOUNT, account)
        putSerializable(KEY_ORDER, order)
        putArray(KEY_EXTENSIONS, extensions)
    }

}