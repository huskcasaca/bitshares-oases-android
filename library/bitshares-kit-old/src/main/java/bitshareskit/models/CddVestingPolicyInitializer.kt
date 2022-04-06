package bitshareskit.models

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.optGrapheneTime
import bitshareskit.extensions.optUInt
import bitshareskit.objects.GrapheneSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONObject
import java.util.*

data class CddVestingPolicyInitializer(
    val startClaim: Date,
    val vestingSeconds: UInt
): GrapheneSerializable {

//            start_claim: time_point_sec,
//            vesting_seconds: uint32

    companion object {

        const val KEY_START_CLAIM = "start_claim"
        const val KEY_VESTING_SECONDS = "vesting_seconds"

        fun fromJson(rawJson: JSONObject): CddVestingPolicyInitializer {
            return CddVestingPolicyInitializer(
                rawJson.optGrapheneTime(KEY_START_CLAIM),
                rawJson.optUInt(KEY_VESTING_SECONDS)
            )
        }
    }


    override fun toByteArray(): ByteArray = buildPacket {

    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {


    }

}