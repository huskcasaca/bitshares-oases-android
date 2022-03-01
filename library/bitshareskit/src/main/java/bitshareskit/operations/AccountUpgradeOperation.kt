package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneBoolean
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AccountUpgradeOperation(
    var account: AccountObject,
    val isLifetime: Boolean
): Operation() {

    /*{
        "fee":{
            "amount":"20000000",
            "asset_id":"1.3.0"
        },
        "account_to_upgrade":"1.2.25563",
        "upgrade_to_lifetime_member":true,
        "extensions":[
        ]
    }*/

    companion object {

        const val KEY_ACCOUNT_TO_UPGRADE = "account_to_upgrade"
        const val KEY_UPGRADE_TO_LIFETIME_MEMBER = "upgrade_to_lifetime_member"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AccountUpgradeOperation {
            return AccountUpgradeOperation(
                rawJson.optGrapheneInstance(KEY_ACCOUNT_TO_UPGRADE),
                rawJson.optBoolean(KEY_UPGRADE_TO_LIFETIME_MEMBER)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ACCOUNT_UPGRADE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeSerializable(account)
        writeGrapheneBoolean(isLifetime)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putSerializable(KEY_ACCOUNT_TO_UPGRADE, account)
        putItem(KEY_UPGRADE_TO_LIFETIME_MEMBER, isLifetime)
        putArray(KEY_EXTENSIONS, extensions)
    }

}