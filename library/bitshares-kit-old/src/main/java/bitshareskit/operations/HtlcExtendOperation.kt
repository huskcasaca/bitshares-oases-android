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

data class HtlcExtendOperation(
    val htlc: HtlcObject,
    var issuer: AccountObject,
    val secondSToAdd: UInt
): Operation() {

    companion object {
//    export const htlc_extend = new Serializer("htlc_extend", {
//        fee: asset,
//        htlc_id: protocol_id_type("htlc"),
//        update_issuer: protocol_id_type("account"),
//        seconds_to_add: uint32,
//

        const val KEY_HTLC_ID = "htlc_id"
        const val KEY_UPDATE_ISSUER = "update_issuer"
        const val KEY_SECONDS_TO_ADD = "seconds_to_add"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): HtlcExtendOperation {
            return HtlcExtendOperation(
                rawJson.optGrapheneInstance(KEY_HTLC_ID),
                rawJson.optGrapheneInstance(KEY_UPDATE_ISSUER),
                rawJson.optUInt(KEY_SECONDS_TO_ADD)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.HTLC_EXTEND_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}