package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.objects.AccountObject
import bitshareskit.objects.VestingBalanceObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class VestingBalanceWithdrawOperation(
    val vestingBalance: VestingBalanceObject,
    var owner: AccountObject,
    var amount: AssetAmount
): Operation() {

//    vesting_balance: protocol_id_type("vesting_balance"),
//    owner: protocol_id_type("account"),
//    amount: asset

    companion object {

        const val KEY_OWNER = "owner"
        const val KEY_VESTING_BALANCE = "vesting_balance"
        const val KEY_AMOUNT = "amount"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): VestingBalanceWithdrawOperation {
            return VestingBalanceWithdrawOperation(
                rawJson.optGrapheneInstance(KEY_VESTING_BALANCE),
                rawJson.optGrapheneInstance(KEY_OWNER),
                rawJson.optItem(KEY_AMOUNT)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.VESTING_BALANCE_WITHDRAW_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}