package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.PublicKey
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import bitshareskit.objects.WitnessObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class WitnessUpdateOperation(
    var witness: WitnessObject,
    var account: AccountObject,
    val url: String,
    val key: PublicKey
): Operation() {

    companion object {

        const val KEY_WITNESS = "witness"
        const val KEY_WITNESS_ACCOUNT = "witness_account"
        const val KEY_NEW_URL = "new_url"
        const val KEY_NEW_SIGNING_KEY = "new_signing_key"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): WitnessUpdateOperation {
            return WitnessUpdateOperation(
                rawJson.optGrapheneInstance(KEY_WITNESS),
                rawJson.optGrapheneInstance(KEY_WITNESS_ACCOUNT),
                rawJson.optString(KEY_NEW_URL),
                rawJson.optPublicKey(KEY_NEW_SIGNING_KEY)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    var result: AssetObject = createGrapheneEmptyInstance()

    override val operationType = OperationType.WITNESS_UPDATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}