package bitshareskit.models

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.optPublicKey
import bitshareskit.objects.GrapheneSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONObject

data class StealthConfirmation(
    val oneTimeKey: PublicKey,
    val to: PublicKey?,
    val encryptedMemo: String
): GrapheneSerializable {

    companion object {

//        one_time_key: public_key,
//        to: optional(public_key),
//        encrypted_memo: bytes()

        const val KEY_ONE_TIME_KEY = "one_time_key"
        const val KEY_TO = "to"
        const val KEY_ENCRYPTED_MEMO = "encrypted_memo"

        fun fromJson(rawJson: JSONObject): StealthConfirmation {
            return StealthConfirmation(
                rawJson.optPublicKey(KEY_ONE_TIME_KEY),
                rawJson.optPublicKey(KEY_TO),
                rawJson.optString(KEY_ENCRYPTED_MEMO)
            )
        }
    }


    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {


    }

}