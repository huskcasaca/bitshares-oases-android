package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class CommitteeMemberCreateOperation(
    var account: AccountObject,
    val url: String
): Operation() {

    companion object {

        const val KEY_COMMITTEE_MEMBER_ACCOUNT = "committee_member_account"
        const val KEY_URL = "url"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): CommitteeMemberCreateOperation {
            return CommitteeMemberCreateOperation(
                rawJson.optGrapheneInstance(KEY_COMMITTEE_MEMBER_ACCOUNT),
                rawJson.optString(KEY_URL)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.COMMITTEE_MEMBER_CREATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}