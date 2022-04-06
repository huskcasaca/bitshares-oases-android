package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.objects.AccountObject
import bitshareskit.objects.ForceSettlementObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AssetSettleCancelOperation(
    val settlement: ForceSettlementObject,
    var account: AccountObject,
    var amount: AssetAmount
): Operation() {

    companion object {

//        settlement: protocol_id_type("force_settlement"),
//        account: protocol_id_type("account"),
//        amount: asset,

        const val KEY_SETTLEMENT = "settlement"
        const val KEY_ACCOUNT = "account"
        const val KEY_AMOUNT = "amount"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetSettleCancelOperation {
            return AssetSettleCancelOperation(
                rawJson.optGrapheneInstance(KEY_SETTLEMENT),
                rawJson.optGrapheneInstance(KEY_ACCOUNT),
                rawJson.optItem(KEY_AMOUNT)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSET_SETTLE_CANCEL_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}