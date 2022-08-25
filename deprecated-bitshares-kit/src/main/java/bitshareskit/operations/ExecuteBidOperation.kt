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

data class ExecuteBidOperation(
    var bidder: AccountObject,
    var debt: AssetAmount,
    var collateral: AssetAmount
): Operation() {

    companion object {
//        bidder: protocol_id_type("account"),
//        debt: asset,
//        collateral: asset

        const val KEY_BIDDER = "bidder"
        const val KEY_DEBT = "debt"
        const val KEY_COLLATERAL = "collateral"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): ExecuteBidOperation {
            return ExecuteBidOperation(
                rawJson.optGrapheneInstance(KEY_BIDDER),
                rawJson.optItem(KEY_DEBT),
                rawJson.optItem(KEY_COLLATERAL)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.EXECUTE_BID_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}