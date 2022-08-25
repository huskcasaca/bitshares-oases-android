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
import java.util.*

data class WithdrawPermissionCreateOperation(
    var from: AccountObject,
    var authorized: AccountObject,
    var limit: AssetAmount,
    val period: UInt,
    val periodsUntilExpiration: UInt,
    val periodStartTime: Date
): Operation() {

    companion object {

//        fee: asset,
//        withdraw_from_account: protocol_id_type("account"),
//        authorized_account: protocol_id_type("account"),
//        withdrawal_limit: asset,
//        withdrawal_period_sec: uint32,
//        periods_until_expiration: uint32,
//        period_start_time: time_point_sec

        const val KEY_WITHDRAW_FROM_ACCOUNT = "withdraw_from_account"
        const val KEY_AUTHORIZED_ACCOUNT = "authorized_account"
        const val KEY_WITHDRAWAL_LIMIT = "withdrawal_limit"
        const val KEY_WITHDRAWAL_PERIOD_SEC = "withdrawal_period_sec"
        const val KEY_PERIODS_UNTIL_EXPIRATION = "periods_until_expiration"
        const val KEY_PERIOD_START_TIME = "period_start_time"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): WithdrawPermissionCreateOperation {
            return WithdrawPermissionCreateOperation(
                rawJson.optGrapheneInstance(KEY_WITHDRAW_FROM_ACCOUNT),
                rawJson.optGrapheneInstance(KEY_AUTHORIZED_ACCOUNT),
                rawJson.optItem(KEY_WITHDRAWAL_LIMIT),
                rawJson.optUInt(KEY_WITHDRAWAL_PERIOD_SEC),
                rawJson.optUInt(KEY_PERIODS_UNTIL_EXPIRATION),
                rawJson.optGrapheneTime(KEY_PERIOD_START_TIME)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.WITHDRAW_PERMISSION_CREATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}