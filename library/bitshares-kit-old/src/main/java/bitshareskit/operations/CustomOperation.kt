package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class CustomOperation(
    var account: AccountObject,
    val requiredAuths: Set<AccountObject>,
    val id: UShort,
    val data: String
): Operation() {

    companion object {

//        payer: protocol_id_type("account"),
//        required_auths: set(protocol_id_type("account")),
//        id: uint16,
//        data: bytes()

        const val KEY_PAYER = "payer"
        const val KEY_REQUIRED_AUTHS = "required_auths"
        const val KEY_ID = "id"
        const val KEY_DATA = "data"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): CustomOperation {
            return CustomOperation(
                rawJson.optGrapheneInstance(KEY_PAYER),
                rawJson.optIterable<String>(KEY_REQUIRED_AUTHS).map { createGraphene<AccountObject>(it) }.toSet(),
                rawJson.optUShort(KEY_ID),
                rawJson.optString(KEY_DATA)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.CUSTOM_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}