package bitshareskit.models

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.optItem
import bitshareskit.objects.GrapheneSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONObject

data class BlindOutput(
    val commitment: String,
    val rangeProof: String,
    val owner: Authority,
    val stealthMemo: StealthConfirmation?
): GrapheneSerializable {

    companion object {

//        commitment: bytes(33),
//        range_proof: bytes(),
//        owner: authority,
//        stealth_memo: optional(stealth_confirmation)

        const val KEY_COMMITMENT = "commitment"
        const val KEY_RANGE_PROOF = "range_proof"
        const val KEY_OWNER = "owner"
        const val KEY_STEALTH_MEMO = "stealth_memo"


        fun fromJson(rawJson: JSONObject): BlindOutput {
            return BlindOutput(
                rawJson.optString(KEY_COMMITMENT),
                rawJson.optString(KEY_RANGE_PROOF),
                rawJson.optItem(KEY_OWNER),
                rawJson.optItem(KEY_STEALTH_MEMO)
            )
        }
    }


    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
    }

}