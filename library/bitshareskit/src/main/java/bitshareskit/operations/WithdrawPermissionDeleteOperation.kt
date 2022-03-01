package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.objects.WithdrawPermissionObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class WithdrawPermissionDeleteOperation(
    var authorized: AccountObject,
    var from: AccountObject,
    val permission: WithdrawPermissionObject
): Operation() {

    companion object {

//        withdraw_from_account: protocol_id_type("account"),
//        authorized_account: protocol_id_type("account"),
//        withdrawal_permission: protocol_id_type("withdraw_permission")

        const val KEY_WITHDRAW_FROM_ACCOUNT = "withdraw_from_account"
        const val KEY_AUTHORIZED_ACCOUNT = "authorized_account"
        const val KEY_WITHDRAWAL_PERMISSION = "withdrawal_permission"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): WithdrawPermissionDeleteOperation {
            return WithdrawPermissionDeleteOperation(
                rawJson.optGrapheneInstance(KEY_WITHDRAW_FROM_ACCOUNT),
                rawJson.optGrapheneInstance(KEY_AUTHORIZED_ACCOUNT),
                rawJson.optGrapheneInstance(KEY_WITHDRAWAL_PERMISSION)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.WITHDRAW_PERMISSION_DELETE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}