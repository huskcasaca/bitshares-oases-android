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

data class ProposalDeleteOperation(
    var account: AccountObject,
    val isOwner: Boolean,
    val proposal: ProposalObject
): Operation() {

    companion object {

        const val KEY_FEE_PAYING_ACCOUNT = "fee_paying_account"
        const val KEY_USING_OWNER_AUTHORITY = "using_owner_authority"
        const val KEY_PROPOSAL = "proposal"

        // TODO: result
        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): ProposalDeleteOperation {
            return ProposalDeleteOperation(
                rawJson.optGrapheneInstance(KEY_FEE_PAYING_ACCOUNT),
                rawJson.optBoolean(KEY_USING_OWNER_AUTHORITY),
                rawJson.optGrapheneInstance(KEY_PROPOSAL)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.PROPOSAL_DELETE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}