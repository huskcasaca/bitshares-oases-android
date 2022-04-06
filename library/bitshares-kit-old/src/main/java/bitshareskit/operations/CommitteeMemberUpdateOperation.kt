package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.objects.CommitteeMemberObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class CommitteeMemberUpdateOperation(
    var committee: CommitteeMemberObject,
    var account: AccountObject,
    val url: String
): Operation() {

    companion object {

        const val KEY_COMMITTEE_MEMBER = "committee_member"
        const val KEY_COMMITTEE_MEMBER_ACCOUNT = "committee_member_account"
        const val KEY_NEW_URL = "new_url"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): CommitteeMemberUpdateOperation {
            return CommitteeMemberUpdateOperation(
                rawJson.optGrapheneInstance(KEY_COMMITTEE_MEMBER),
                rawJson.optGrapheneInstance(KEY_COMMITTEE_MEMBER_ACCOUNT),
                rawJson.optString(KEY_NEW_URL)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.COMMITTEE_MEMBER_UPDATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}