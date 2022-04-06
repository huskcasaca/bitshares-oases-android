package bitshareskit.models

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.optItem
import bitshareskit.objects.GrapheneSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONObject

data class BlindInput(
    val commitment: String,
    val owner: Authority
): GrapheneSerializable {

    companion object {
//        commitment: bytes(33),
//        owner: authority

        const val KEY_COMMITMENT = "commitment"
        const val KEY_OWNER = "owner"

        fun fromJson(rawJson: JSONObject): BlindInput {
            return BlindInput(
                rawJson.optString(KEY_COMMITMENT),
                rawJson.optItem(KEY_OWNER)
            )
        }
    }


    override fun toByteArray(): ByteArray = buildPacket {

    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {


    }

}