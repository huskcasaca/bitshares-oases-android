package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AccountTransferOperation(
    var account: AccountObject,
    var newOwner: AccountObject
): Operation() {

    companion object {

        const val KEY_ACCOUNT_ID = "account_id"
        const val KEY_NEW_OWNER = "new_owner"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AccountTransferOperation {
            return AccountTransferOperation(
                rawJson.optGrapheneInstance(KEY_ACCOUNT_ID),
                rawJson.optGrapheneInstance(KEY_NEW_OWNER)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ACCOUNT_TRANSFER_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}