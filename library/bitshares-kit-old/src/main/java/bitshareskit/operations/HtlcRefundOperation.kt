package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.objects.HtlcObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class HtlcRefundOperation(
    val htlc: HtlcObject,
    var to: AccountObject
): Operation() {

    companion object {
//    export const htlc_refund = new Serializer("htlc_refund", {
//        fee: asset,
//        htlc_id: protocol_id_type("htlc"),
//        to: protocol_id_type("account")

        const val KEY_HTLC_ID = "htlc_id"
        const val KEY_TO = "to"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): HtlcRefundOperation {
            return HtlcRefundOperation(
                rawJson.optGrapheneInstance(KEY_HTLC_ID),
                rawJson.optGrapheneInstance(KEY_TO)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.HTLC_REFUND_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}