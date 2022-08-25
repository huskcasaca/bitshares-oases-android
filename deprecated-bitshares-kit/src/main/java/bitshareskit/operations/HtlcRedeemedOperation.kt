package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.objects.AccountObject
import bitshareskit.objects.HtlcObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class HtlcRedeemedOperation(
    val htlc: HtlcObject,
    var from: AccountObject,
    var to: AccountObject,
    var amount: AssetAmount
): Operation() {

    companion object {
//    export const htlc_redeemed = new Serializer("htlc_redeemed", {
//        fee: asset,
//        htlc_id: protocol_id_type("htlc"),
//        from: protocol_id_type("account"),
//        to: protocol_id_type("account"),
//        amount: asset
//

        const val KEY_HTLC_ID = "htlc_id"
        const val KEY_FROM = "from"
        const val KEY_TO = "to"
        const val KEY_AMOUNT = "amount"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): HtlcRedeemedOperation {
            return HtlcRedeemedOperation(
                rawJson.optGrapheneInstance(KEY_HTLC_ID),
                rawJson.optGrapheneInstance(KEY_FROM),
                rawJson.optGrapheneInstance(KEY_TO),
                rawJson.optItem(KEY_AMOUNT)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.HTLC_REDEEMED_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}