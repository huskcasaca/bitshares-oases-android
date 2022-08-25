package bitshareskit.models

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.optGrapheneTime
import bitshareskit.extensions.optUInt
import bitshareskit.objects.GrapheneSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONObject
import java.util.*

data class LinearVestingPolicyInitializer(
    val beginTimestamp: Date,
    val vestingCliffSeconds: UInt,
    val vestingDurationSeconds: UInt
    ): GrapheneSerializable {

//            begin_timestamp: time_point_sec,
//            vesting_cliff_seconds: uint32,
//            vesting_duration_seconds: uint32

    companion object {

        const val KEY_BEGIN_TIMESTAMP = "begin_timestamp"
        const val KEY_VESTING_CLIFF_SECONDS = "vesting_cliff_seconds"
        const val KEY_VESTING_DURATION_SECONDS = "vesting_duration_seconds"

        fun fromJson(rawJson: JSONObject): LinearVestingPolicyInitializer {
            return LinearVestingPolicyInitializer(
                rawJson.optGrapheneTime(KEY_BEGIN_TIMESTAMP),
                rawJson.optUInt(KEY_VESTING_CLIFF_SECONDS),
                rawJson.optUInt(KEY_VESTING_DURATION_SECONDS)
            )
        }
    }


    override fun toByteArray(): ByteArray = buildPacket {

    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {


    }

}