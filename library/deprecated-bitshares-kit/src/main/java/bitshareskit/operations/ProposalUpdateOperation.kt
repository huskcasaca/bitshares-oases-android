package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.PublicKey
import bitshareskit.objects.AccountObject
import bitshareskit.objects.ProposalObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class ProposalUpdateOperation(
    var account: AccountObject,
    val proposal: ProposalObject,
    val activeToAdd: Set<AccountObject>,
    val activeToRemove: Set<AccountObject>,
    val ownerToAdd: Set<AccountObject>,
    val ownerToRemove: Set<AccountObject>,
    val keyToAdd: Set<PublicKey>,
    val keyToRemove: Set<PublicKey>
): Operation() {

    companion object {

//        fee: asset,
//        fee_paying_account: protocol_id_type("account"),
//        proposal: protocol_id_type("proposal"),
//        active_approvals_to_add: set(protocol_id_type("account")),
//        active_approvals_to_remove: set(protocol_id_type("account")),
//        owner_approvals_to_add: set(protocol_id_type("account")),
//        owner_approvals_to_remove: set(protocol_id_type("account")),
//        key_approvals_to_add: set(public_key),
//        key_approvals_to_remove: set(public_key),
//        extensions: set(future_extensions)

        const val KEY_FEE_PAYING_ACCOUNT = "fee_paying_account"
        const val KEY_PROPOSAL = "proposal"
        const val KEY_ACTIVE_APPROVALS_TO_ADD = "active_approvals_to_add"
        const val KEY_ACTIVE_APPROVALS_TO_REMOVE = "active_approvals_to_remove"
        const val KEY_OWNER_APPROVALS_TO_ADD = "owner_approvals_to_add"
        const val KEY_OWNER_APPROVALS_TO_REMOVE = "owner_approvals_to_remove"
        const val KEY_KEY_APPROVALS_TO_ADD = "key_approvals_to_add"
        const val KEY_KEY_APPROVALS_TO_REMOVE = "key_approvals_to_remove"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): ProposalUpdateOperation {
            return ProposalUpdateOperation(
                rawJson.optGrapheneInstance(KEY_FEE_PAYING_ACCOUNT),
                rawJson.optGrapheneInstance(KEY_PROPOSAL),
                rawJson.optIterable<String>(KEY_ACTIVE_APPROVALS_TO_ADD).map { createGraphene<AccountObject>(it) }.toSet(),
                rawJson.optIterable<String>(KEY_ACTIVE_APPROVALS_TO_REMOVE).map { createGraphene<AccountObject>(it) }.toSet(),
                rawJson.optIterable<String>(KEY_OWNER_APPROVALS_TO_ADD).map { createGraphene<AccountObject>(it) }.toSet(),
                rawJson.optIterable<String>(KEY_OWNER_APPROVALS_TO_REMOVE).map { createGraphene<AccountObject>(it) }.toSet(),
                rawJson.optIterable<String>(KEY_KEY_APPROVALS_TO_ADD).map { PublicKey.fromAddress(it) }.toSet(),
                rawJson.optIterable<String>(KEY_KEY_APPROVALS_TO_REMOVE).map { PublicKey.fromAddress(it) }.toSet()
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.PROPOSAL_UPDATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}