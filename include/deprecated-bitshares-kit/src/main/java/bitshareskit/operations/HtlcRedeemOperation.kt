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

data class HtlcRedeemOperation(
    val htlc: HtlcObject,
    var redeemer: AccountObject,
    val preimage: String
): Operation() {

    companion object {
//
//    export const htlc_redeem = new Serializer("htlc_redeem", {
//        fee: asset,
//        htlc_id: protocol_id_type("htlc"),
//        redeemer: protocol_id_type("account"),
//        preimage: bytes(),
//

        const val KEY_HTLC_ID = "htlc_id"
        const val KEY_REDEEMER = "redeemer"
        const val KEY_PREIMAGE = "preimage"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): HtlcRedeemOperation {
            return HtlcRedeemOperation(
                rawJson.optGrapheneInstance(KEY_HTLC_ID),
                rawJson.optGrapheneInstance(KEY_REDEEMER),
                rawJson.optString(KEY_PREIMAGE)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.HTLC_REDEEM_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}