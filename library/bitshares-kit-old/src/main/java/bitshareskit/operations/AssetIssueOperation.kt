package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AssetIssueOperation(
    var issuer: AccountObject,
    var amount: AssetAmount,
    var issueTo: AccountObject
): Operation() {

    /*{
        "fee":{
            "amount":"197",
            "asset_id":"1.3.0"
        },
        "issuer":"1.2.25563",
        "asset_to_issue":{
            "amount":"10000000000",
            "asset_id":"1.3.1553"
        },
        "issue_to_account":"1.2.25563",
        "extensions":[
        ]
    }*/

    companion object {

        const val KEY_ISSUER = "issuer"
        const val KEY_ASSET_TO_ISSUE = "asset_to_issue"
        const val KEY_ISSUE_TO_ACCOUNT = "issue_to_account"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetIssueOperation {
            return AssetIssueOperation(
                rawJson.optGrapheneInstance(KEY_ISSUER),
                rawJson.optItem(KEY_ASSET_TO_ISSUE),
                rawJson.optGrapheneInstance(KEY_ISSUE_TO_ACCOUNT)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSET_ISSUE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}