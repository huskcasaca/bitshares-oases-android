package bitshareskit.operations

import bitshareskit.ks_chain.Authority
import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.SimplePrice
import bitshareskit.objects.AccountObject
import bitshareskit.objects.LimitOrderObject
import bitshareskit.serializer.writeGrapheneBoolean
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeGrapheneTime
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.util.*

data class LimitOrderCreateOperation(
    var account: AccountObject,
    var sells: AssetAmount,
    var receives: AssetAmount,
    val expiration: Date = Date(),
    val isFill: Boolean = false // fill or kill
): Operation() {

    /*{
        "fee":{
            "amount":4826,
            "asset_id":"1.3.0"
        },
        "seller":"1.2.1073373",
        "amount_to_sell":{
            "amount":1106292,
            "asset_id":"1.3.4344"
        },
        "min_to_receive":{
            "amount":12107,
            "asset_id":"1.3.4343"
        },
        "expiration":"2020-07-18T04:56:35",
        "fill_or_kill":false,
        "extensions":[
        ]
     }*/

    companion object {
        const val KEY_SELLER = "seller"
        const val KEY_AMOUNT_TO_SELL = "amount_to_sell"
        const val KEY_MIN_TO_RECEIVE = "min_to_receive"
        const val KEY_EXPIRATION = "expiration"
        const val KEY_FILL_OR_KILL = "fill_or_kill"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): LimitOrderCreateOperation {
            return LimitOrderCreateOperation(
                rawJson.optGrapheneInstance(KEY_SELLER),
                rawJson.optItem(KEY_AMOUNT_TO_SELL),
                rawJson.optItem(KEY_MIN_TO_RECEIVE),
                rawJson.optGrapheneTime(KEY_MIN_TO_RECEIVE),
                rawJson.optBoolean(KEY_FILL_OR_KILL)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
                result = rawJsonResult.optGrapheneInstance(1)
            }
        }
    }

    val sellPrice: SimplePrice get() = SimplePrice(receives, sells)
    val receivePrice: SimplePrice get() = SimplePrice(sells, receives)

    var result: LimitOrderObject = createGrapheneEmptyInstance()

    override val operationType = OperationType.LIMIT_ORDER_CREATE_OPERATION

    override val authority: Authority = Authority.ACTIVE

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeSerializable(account)
        writeSerializable(sells)
        writeSerializable(receives)
        writeGrapheneTime(expiration)
        writeGrapheneBoolean(isFill)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putSerializable(KEY_SELLER, account)
        putSerializable(KEY_AMOUNT_TO_SELL, sells)
        putSerializable(KEY_MIN_TO_RECEIVE, receives)
        putGrapheneTime(KEY_EXPIRATION, expiration)
        putArray(KEY_EXTENSIONS, extensions)
    }
}