package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.Memo
import bitshareskit.objects.AccountObject
import bitshareskit.objects.WithdrawPermissionObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class WithdrawPermissionUpdateOperation(
    val permission: WithdrawPermissionObject,
    var from: AccountObject,
    var to: AccountObject,
    var amount: AssetAmount,
    val memo: Memo
): Operation() {

    companion object {

//        withdraw_permission: protocol_id_type("withdraw_permission"),
//        withdraw_from_account: protocol_id_type("account"),
//        withdraw_to_account: protocol_id_type("account"),
//        amount_to_withdraw: asset,
//        memo: optional(memo_data)

        const val KEY_PERMISSION_TO_UPDATE = "permission_to_update"
        const val KEY_WITHDRAW_FROM_ACCOUNT = "withdraw_from_account"
        const val KEY_WITHDRAW_TO_ACCOUNT = "withdraw_to_account"
        const val KEY_AMOUNT_TO_WITHDRAW = "amount_to_withdraw"
        const val KEY_MEMO = "memo"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): WithdrawPermissionUpdateOperation {
            return WithdrawPermissionUpdateOperation(
                rawJson.optGrapheneInstance(KEY_PERMISSION_TO_UPDATE),
                rawJson.optGrapheneInstance(KEY_WITHDRAW_FROM_ACCOUNT),
                rawJson.optGrapheneInstance(KEY_WITHDRAW_TO_ACCOUNT),
                rawJson.optItem(KEY_AMOUNT_TO_WITHDRAW),
                rawJson.optItem(KEY_MEMO)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.WITHDRAW_PERMISSION_UPDATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}