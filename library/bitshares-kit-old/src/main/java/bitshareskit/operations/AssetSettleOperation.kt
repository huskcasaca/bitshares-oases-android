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

data class AssetSettleOperation(
    var account: AccountObject,
    var amount: AssetAmount
): Operation() {

    companion object {

        const val KEY_ACCOUNT = "account"
        const val KEY_AMOUNT = "amount"

        const val INSTANT_SETTLE_CODE = 0x02

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetSettleOperation {
            return AssetSettleOperation(
                rawJson.optGrapheneInstance(KEY_ACCOUNT),
                rawJson.optItem(KEY_AMOUNT)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
                result = rawJsonResult.optInt(1)
            }
        }
    }

    var result = -1

    val isInstantSettle get() = result == INSTANT_SETTLE_CODE

    override val operationType = OperationType.ASSET_SETTLE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}