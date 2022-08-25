package bitshareskit.operations

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optItem
import bitshareskit.models.AssetAmount
import bitshareskit.models.SimplePrice
import bitshareskit.objects.AccountObject
import bitshareskit.objects.GrapheneObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class FillOrderOperation(
    var account: AccountObject,
    var order: GrapheneObject,
    var pays: AssetAmount,
    var receives: AssetAmount,
    var price: SimplePrice,
    var isMaker: Boolean
): Operation() {

    /*{
        "fee":{
            "amount":0,
            "asset_id":"1.3.0"
        },
        "order_id":"1.7.444325349",
        "account_id":"1.2.96393",
        "pays":{
            "amount":110323,
            "asset_id":"1.3.121"
        },
        "totalReceives":{
            "amount":31641036,
            "asset_id":"1.3.0"
        },
        "fill_price":{
            "base":{
                "amount":1500100000,
                "asset_id":"1.3.0"
            },
            "quote":{
                "amount":5230408,
                "asset_id":"1.3.121"
            }
        },
        "is_maker":false
    }*/

    companion object {
        const val KEY_ORDER_ID = "order_id"
        const val KEY_ACCOUNT_ID = "account_id"
        const val KEY_PAYS = "pays"
        const val KEY_RECEIVES = "totalReceives"
        const val KEY_FILL_PRICE = "fill_price"
        const val KEY_IS_MAKER = "is_maker"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): FillOrderOperation {
            return FillOrderOperation(
                rawJson.optGrapheneInstance(KEY_ACCOUNT_ID),
                rawJson.optGrapheneInstance(KEY_ORDER_ID),
                rawJson.optItem(KEY_PAYS),
                rawJson.optItem(KEY_RECEIVES),
                rawJson.optItem(KEY_FILL_PRICE),
                rawJson.optBoolean(KEY_IS_MAKER)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    val payPrice: SimplePrice get() = if (price.base.asset.uid == pays.asset.uid) price.invertedPair else price
    val receivePrice: SimplePrice get() = if (price.quote.asset.uid == receives.asset.uid) price else price.invertedPair

    override val operationType = OperationType.FILL_ORDER_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject {
        return buildJsonObject {
        }
    }
}