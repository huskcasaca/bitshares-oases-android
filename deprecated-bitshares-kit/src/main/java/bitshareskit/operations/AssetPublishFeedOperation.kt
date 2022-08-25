package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.PriceFeed
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AssetPublishFeedOperation(
    var publisher: AccountObject,
    var asset: AssetObject,
    val feed: PriceFeed
): Operation() {

    /*{
        "fee":{
            "amount":"100",
            "asset_id":"1.3.0"
        },
        "payer":"1.2.25563",
        "amount_to_reserve":{
            "amount":"100000",
            "asset_id":"1.3.0"
        },
        "extensions":[
        ]
    }*/

    companion object {

        const val KEY_PUBLISHER = "publisher"
        const val KEY_ASSET_ID = "asset_id"
        const val KEY_FEED = "feed"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetPublishFeedOperation {
            return AssetPublishFeedOperation(
                rawJson.optGrapheneInstance(KEY_PUBLISHER),
                rawJson.optGrapheneInstance(KEY_ASSET_ID),
                rawJson.optItem(KEY_FEED)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSET_PUBLISH_FEED_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}