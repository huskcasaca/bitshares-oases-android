package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.objects.ProposalObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.util.*

data class ProposalCreateOperation(
    var account: AccountObject,
    val expiration: Date,
    val operations: List<Operation>,
    val reviewTime: UInt
): Operation() {

    companion object {

        const val KEY_FEE_PAYING_ACCOUNT = "fee_paying_account"
        const val KEY_EXPIRATION_TIME = "expiration_time"
        const val KEY_PROPOSED_OPS = "proposed_ops"
        const val KEY_REVIEW_PERIOD_SECONDS = "review_period_seconds"

        // TODO: result
        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): ProposalCreateOperation {
            return ProposalCreateOperation(
                rawJson.optGrapheneInstance(KEY_FEE_PAYING_ACCOUNT),
                rawJson.optGrapheneTime(KEY_EXPIRATION_TIME),
                rawJson.optIterable<JSONArray>(KEY_PROPOSED_OPS).map { fromJsonPair(it.optJSONArray(1), JSONArray()) },
                rawJson.optUInt(KEY_REVIEW_PERIOD_SECONDS)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
                result = createGraphene(rawJsonResult.optString(1))
            }
        }
    }

    var result: ProposalObject = createGrapheneEmptyInstance()

    override val operationType = OperationType.PROPOSAL_CREATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}