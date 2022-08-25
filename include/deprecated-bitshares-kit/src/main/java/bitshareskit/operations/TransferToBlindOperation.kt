package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.BlindOutput
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class TransferToBlindOperation(
    var amount: AssetAmount,
    var from: AccountObject,
    val blindingFactor: String,
    val output: List<BlindOutput>
): Operation() {

    companion object {

//        amount: asset,
//        from: protocol_id_type("account"),
//        blinding_factor: bytes(32),
//        outputs: array(blind_output)

        const val KEY_AMOUNT = "amount"
        const val KEY_FROM = "from"
        const val KEY_BLINDING_FACTOR = "blinding_factor"
        const val KEY_OUTPUTS = "outputs"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): TransferToBlindOperation {
            return TransferToBlindOperation(
                rawJson.optItem(KEY_AMOUNT),
                rawJson.optGrapheneInstance(KEY_FROM),
                rawJson.optString(KEY_BLINDING_FACTOR),
                rawJson.optIterable<JSONObject>(KEY_OUTPUTS).map { BlindOutput.fromJson(it) }
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.TRANSFER_TO_BLIND_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}