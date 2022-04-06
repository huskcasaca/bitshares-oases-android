package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.Memo
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class OverrideTransferOperation(
    var issuer: AccountObject,
    var from: AccountObject,
    var to: AccountObject,
    var amount: AssetAmount,
    val memo: Memo?
): Operation() {

    companion object {
//        issuer: protocol_id_type("account"),
//        from: protocol_id_type("account"),
//        to: protocol_id_type("account"),
//        amount: asset,
//        memo: optional(memo_data),

        const val KEY_issuer = "issuer"
        const val KEY_FROM = "from"
        const val KEY_TO = "to"
        const val KEY_AMOUNT = "amount"
        const val KEY_MEMO = "memo"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): OverrideTransferOperation {
            return OverrideTransferOperation(
                rawJson.optGrapheneInstance(KEY_issuer),
                rawJson.optGrapheneInstance(KEY_FROM),
                rawJson.optGrapheneInstance(KEY_TO),
                rawJson.optItem(KEY_AMOUNT),
                rawJson.optItem(KEY_MEMO)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.OVERRIDE_TRANSFER_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}