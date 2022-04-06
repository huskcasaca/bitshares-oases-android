package bitshareskit.operations

import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import bitshareskit.models.ChainParameters
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class CommitteeMemberUpdateGlobalParametersOperation(
    val newParameters: ChainParameters
): Operation() {

    companion object {

        const val KEY_NEW_PARAMETERS = "new_parameters"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): CommitteeMemberUpdateGlobalParametersOperation {
            return CommitteeMemberUpdateGlobalParametersOperation(
                rawJson.optItem(KEY_NEW_PARAMETERS)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    var committee: AccountObject = createGraphene<AccountObject>(ChainConfig.GLOBAL_INSTANCE)

    override val operationType = OperationType.COMMITTEE_MEMBER_UPDATE_GLOBAL_PARAMETERS_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}