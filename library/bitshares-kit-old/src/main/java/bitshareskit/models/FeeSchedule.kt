package bitshareskit.models

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.optIterable
import bitshareskit.extensions.optUInt
import bitshareskit.objects.GrapheneSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class FeeSchedule(
    val parameters: Set<FeeParameters>,
    val scale: UInt
) : GrapheneSerializable {

    companion object {

        const val KEY_PARAMETERS = "parameters"
        const val KEY_SCALE = "scale"

        fun fromJson(rawJson: JSONObject): FeeSchedule {
            return FeeSchedule(
                rawJson.optIterable<JSONArray>(KEY_PARAMETERS).map { FeeParameters.fromJsonPair(it) }.toSet(),
                rawJson.optUInt(KEY_SCALE)
            )
        }
    }

    override fun toByteArray(): ByteArray = buildPacket {

    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {

    }

}