package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AssetUpdateBitassetOperation(
    var issuer: AccountObject,
    var asset: AssetObject
): Operation() {

    /*{
        "fee":{
            "amount":"100",
            "asset_id":"1.3.0"
        },
        "issuer":"1.2.25563",
        "asset_to_update":"1.3.1552",
        "new_options":{
            "feed_lifetime_sec":8640,
            "minimum_feeds":7,
            "force_settlement_delay_sec":86400,
            "force_settlement_offset_percent":100,
            "maximum_force_settlement_volume":2000,
            "short_backing_asset":"1.3.0",
            "extensions":[
            ]
        },
        "extensions":[
        ]
    }*/

    companion object {


        const val KEY_ISSUER = "issuer"
        const val KEY_ASSET_TO_UPDATE = "asset_to_update"
        const val KEY_NEW_OPTIONS = "new_options"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetUpdateBitassetOperation {
            return AssetUpdateBitassetOperation(
                rawJson.optGrapheneInstance(KEY_ISSUER),
                rawJson.optGrapheneInstance(KEY_ASSET_TO_UPDATE)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSET_UPDATE_BITASSET_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}