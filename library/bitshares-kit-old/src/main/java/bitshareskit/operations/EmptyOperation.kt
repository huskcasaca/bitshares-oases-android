package bitshareskit.operations

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.optItem
import bitshareskit.extensions.putArray
import bitshareskit.extensions.putSerializable
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class EmptyOperation(
    val rawJson: JSONObject
): Operation() {

    companion object {

        const val KEY_ = ""

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): EmptyOperation {
            return EmptyOperation(rawJson).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.UNDEFINED_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}