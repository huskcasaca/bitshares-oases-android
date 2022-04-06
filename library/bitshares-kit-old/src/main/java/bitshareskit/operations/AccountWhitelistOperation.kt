package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeGrapheneUByte
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AccountWhitelistOperation(
    var account: AccountObject,
    var accountToList: AccountObject,
    var method: UByte
): Operation() {

    /*
    {
        "fee":{
            "amount":"100",
            "asset_id":"1.3.0"
        },
        "authorizing_account":"1.2.25563",
        "account_to_list":"1.2.479",
        "new_listing":2,
        "extensions":[
        ]
    }*/

    companion object {

        const val KEY_AUTHORIZING_ACCOUNT = "authorizing_account"
        const val KEY_ACCOUNT_TO_LIST = "account_to_list"
        const val KEY_NEW_LISTING = "new_listing"

        const val REMOVE_BLACKLIST: UByte = 0x00U
        const val ADD_WHITELIST: UByte = 0x01U
        const val REMOVE_WHITELIST: UByte = 0x00U
        const val ADD_BLACKLIST: UByte = 0x02U

        const val NO_LISTING: UByte = 0x00U // No opinion is specified about this account
        const val WHITE_LISTED: UByte = 0x01U // This account is whitelisted, but not blacklisted
        const val BLACK_LISTED: UByte = 0x02U // This account is blacklisted, but not whitelisted
        const val WHITE_AND_BLACK_LISTED: UByte = 0x03U // This account is both whitelisted and blacklisted

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AccountWhitelistOperation {
            return AccountWhitelistOperation(
                rawJson.optGrapheneInstance(KEY_AUTHORIZING_ACCOUNT),
                rawJson.optGrapheneInstance(KEY_ACCOUNT_TO_LIST),
                rawJson.optUByte(KEY_NEW_LISTING)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ACCOUNT_WHITELIST_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeSerializable(account)
        writeSerializable(accountToList)
        writeGrapheneUByte(method)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putSerializable(KEY_AUTHORIZING_ACCOUNT, account)
        putSerializable(KEY_ACCOUNT_TO_LIST, accountToList)
        putNumber(KEY_NEW_LISTING, method)
        putArray(KEY_EXTENSIONS, extensions)
    }

}