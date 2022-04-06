package bitshareskit.models

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.optItem
import bitshareskit.objects.GrapheneSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONObject

data class VestingPolicyInitializer(
    val linearVestingPolicyInitializer: LinearVestingPolicyInitializer,
    val cddVestingPolicyInitializer: CddVestingPolicyInitializer
): GrapheneSerializable {


    companion object {

        const val KEY_LINEAR_VESTING_POLICY_INITIALIZER = "linear_vesting_policy_initializer"
        const val KEY_CDD_VESTING_POLICY_INITIALIZER = "cdd_vesting_policy_initializer"

        fun fromJson(rawJson: JSONObject): VestingPolicyInitializer {
            return VestingPolicyInitializer(
                rawJson.optItem(KEY_LINEAR_VESTING_POLICY_INITIALIZER),
                rawJson.optItem(KEY_CDD_VESTING_POLICY_INITIALIZER)
            )
        }
    }


    override fun toByteArray(): ByteArray = buildPacket {

    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {


    }

}