package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.BlindInput
import bitshareskit.models.BlindOutput
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class BlindTransferOperation(
    val inputs: List<BlindInput>,
    val outputs: List<BlindOutput>
): Operation() {

    companion object {
//        inputs: array(blind_input),
//        outputs: array(blind_output)

        const val KEY_INPUTS = "inputs"
        const val KEY_OUTPUTS = "outputs"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): BlindTransferOperation {
            return BlindTransferOperation(
                rawJson.optIterable<JSONObject>(KEY_INPUTS).map { BlindInput.fromJson(it) },
                rawJson.optIterable<JSONObject>(KEY_OUTPUTS).map { BlindOutput.fromJson(it) }
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.BLIND_TRANSFER_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}