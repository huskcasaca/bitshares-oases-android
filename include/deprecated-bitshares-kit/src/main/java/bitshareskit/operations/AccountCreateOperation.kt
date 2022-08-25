package bitshareskit.operations

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.createGrapheneEmptyInstance
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optItem
import bitshareskit.models.AccountOptions
import bitshareskit.models.Authority
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AccountCreateOperation(
    var registrar: AccountObject,
    var referrer: AccountObject,
    var referrerPercent: Int,
    var name: String,
    var owner: Authority,
    var active: Authority,
    var options: AccountOptions
): Operation() {
    /*{
        "fee":{
            "amount":487557,
            "asset_id":"1.3.0"
        },
        "registrar":"1.2.466046",
        "referrer":"1.2.466046",
        "referrer_percent":0,
        "name":"test-60",
        "owner":{
            "weight_threshold":1,
            "account_auths":[
            ],
            "key_auths":[
                [
                    "BTS4yQSys1Y67z4i7jB13mybWX8neisryNeJC46riNSJ215WP6wxp",
                    1]],
            "address_auths":[
            ]
        },
        "active":{
            "weight_threshold":1,
            "account_auths":[
            ],
            "key_auths":[
                [
                    "BTS83uGfDYCaKg5t6ET1oPtUtT7Uf6pVZSubyb9iUjJg9TnMpzmK5",
                    1]],
            "address_auths":[
            ]
        },
        "options":{
            "memo_key":"BTS83uGfDYCaKg5t6ET1oPtUtT7Uf6pVZSubyb9iUjJg9TnMpzmK5",
            "voting_account":"1.2.5",
            "num_witness":0,
            "num_committee":0,
            "votes":[
            ],
            "extensions":[
            ]
        },
        "extensions":{
        }
        "operation_results":[
            [
                1,
                "1.2.1789432"]
            ]
    }*/

    companion object {
        const val KEY_REGISTRAR = "registrar"
        const val KEY_REFERRER = "referrer"
        const val KEY_REFERRER_PERCENT = "referrer_percent"
        const val KEY_NAME = "name"
        const val KEY_OWNER = "owner"
        const val KEY_ACTIVE = "active"
        const val KEY_OPTIONS = "options"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AccountCreateOperation {
            return AccountCreateOperation(
                rawJson.optGrapheneInstance(KEY_REGISTRAR),
                rawJson.optGrapheneInstance(KEY_REFERRER),
                rawJson.optInt(KEY_REFERRER_PERCENT),
                rawJson.optString(KEY_NAME),
                rawJson.optItem(KEY_OWNER),
                rawJson.optItem(KEY_ACTIVE),
                rawJson.optItem(KEY_OPTIONS)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
                result = rawJsonResult.optGrapheneInstance(1)
            }
        }
    }


    var result: AccountObject = createGrapheneEmptyInstance()

    override val operationType = OperationType.ACCOUNT_CREATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject {
        return buildJsonObject {
        }
    }
}