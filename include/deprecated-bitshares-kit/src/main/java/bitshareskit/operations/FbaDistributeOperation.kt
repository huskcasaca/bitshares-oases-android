package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.objects.FbaAccumulatorObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class FbaDistributeOperation(
    var account: AccountObject,
    val fba: FbaAccumulatorObject,
    val amount: Long
): Operation() {

    companion object {

//        account_id: protocol_id_type("account"),
//        fba_id: protocol_id_type("fba_accumulator"),
//        amount: int64

        const val KEY_ACCOUNT_ID = "account_id"
        const val KEY_FBA_ID = "fba_id"
        const val KEY_AMOUNT = "amount"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): FbaDistributeOperation {
            return FbaDistributeOperation(
                rawJson.optGrapheneInstance(KEY_ACCOUNT_ID),
                rawJson.optGrapheneInstance(KEY_FBA_ID),
                rawJson.optLong(KEY_AMOUNT)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.FBA_DISTRIBUTE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}