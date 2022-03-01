package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.BlindInput
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class TransferFromBlindOperation(
    var amount: AssetAmount,
    var to: AccountObject,
    val blindingFactor: String,
    val inputs: List<BlindInput>
): Operation() {

    companion object {

//        amount: asset,
//        to: protocol_id_type("account"),
//        blinding_factor: bytes(32),
//        inputs: array(blind_input)

        const val KEY_AMOUNT = "amount"
        const val KEY_TO = "to"
        const val KEY_BLINDING_FACTOR = "blinding_factor"
        const val KEY_INPUTS = "inputs"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): TransferFromBlindOperation {
            return TransferFromBlindOperation(
                rawJson.optItem(KEY_AMOUNT),
                rawJson.optGrapheneInstance(KEY_TO),
                rawJson.optString(KEY_BLINDING_FACTOR),
                rawJson.optIterable<JSONObject>(KEY_INPUTS).map { BlindInput.fromJson(it) }
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.TRANSFER_FROM_BLIND_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}
