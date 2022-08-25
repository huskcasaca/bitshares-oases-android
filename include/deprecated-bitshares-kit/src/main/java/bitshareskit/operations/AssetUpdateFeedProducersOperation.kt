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

data class AssetUpdateFeedProducersOperation(
    var issuer: AccountObject,
    var asset: AssetObject,
    val feedProducers: List<AccountObject>
): Operation() {

    /*{
        "fee":{
            "amount":"100",
            "asset_id":"1.3.0"
        },
        "issuer":"1.2.25563",
        "asset_to_update":"1.3.1552",
        "new_feed_producers":[
            "1.2.4070",
            "1.2.4108"],
        "extensions":[
        ]
    }*/

    companion object {


        const val KEY_ISSUER = "issuer"
        const val KEY_ASSET_TO_UPDATE = "asset_to_update"
        const val KEY_NEW_FEED_PRODUCERS = "new_feed_producers"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetUpdateFeedProducersOperation {
            return AssetUpdateFeedProducersOperation(
                rawJson.optGrapheneInstance(KEY_ISSUER),
                rawJson.optGrapheneInstance(KEY_ASSET_TO_UPDATE),
                rawJson.optIterable<String>(KEY_NEW_FEED_PRODUCERS).map { createGraphene(it) }
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSET_UPDATE_FEED_PRODUCERS_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}