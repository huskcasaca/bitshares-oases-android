package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AccountOptions
import bitshareskit.models.Authority
import bitshareskit.models.Optional
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AccountUpdateOperation(
    var account: AccountObject,
    val owner: Optional<Authority> = Optional(),
    val active: Optional<Authority> = Optional(),
    val options: Optional<AccountOptions> = Optional(),
): Operation() {

    constructor(
        account: AccountObject,
        owner: Authority? = null,
        active: Authority? = null,
        options: AccountOptions? = null,
    ) : this (
        account,
        Optional(owner),
        Optional(active),
        Optional(options)
    )

    companion object {

        const val KEY_ACCOUNT = "account"
        const val KEY_OWNER = "owner"
        const val KEY_ACTIVE = "active"
        const val KEY_NEW_OPTIONS = "new_options"
        const val KEY_EXTENSIONS = "extensions"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AccountUpdateOperation {
            return AccountUpdateOperation(
                rawJson.optGrapheneInstance(KEY_ACCOUNT),
                rawJson.optOptionalItem(KEY_OWNER),
                rawJson.optOptionalItem(KEY_ACTIVE),
                rawJson.optOptionalItem(KEY_NEW_OPTIONS)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ACCOUNT_UPDATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeSerializable(account)
        writeSerializable(owner)
        writeSerializable(active)
        writeSerializable(options)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putSerializable(KEY_ACCOUNT, account)
        putSerializable(KEY_OWNER, owner)
        putSerializable(KEY_ACTIVE, active)
        putSerializable(KEY_NEW_OPTIONS, options)
        putArray(KEY_EXTENSIONS, extensions)
    }

}