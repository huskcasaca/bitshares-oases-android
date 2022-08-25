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

data class AssetClaimFeesOperation(
    var issuer: AccountObject,
    var amount: AssetAmount
): Operation() {

    companion object {

//        issuer: protocol_id_type("account"),
//        amount_to_claim: asset,

        const val KEY_ISSUER = "issuer"
        const val KEY_AMOUNT_TO_CLAIM = "amount_to_claim"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetClaimFeesOperation {
            return AssetClaimFeesOperation(
                rawJson.optGrapheneInstance(KEY_ISSUER),
                rawJson.optItem(KEY_AMOUNT_TO_CLAIM)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSET_CLAIM_FEES_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}