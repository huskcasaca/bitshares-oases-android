package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.objects.AccountObject
import bitshareskit.objects.HtlcObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.util.*

@Suppress("EXPERIMENTAL_API_USAGE")
data class HtlcCreateOperation(
    var from: AccountObject,
    var to: AccountObject,
    var amount: AssetAmount,
    val preimageHash: List<String>,
    val preimageSize: UShort,
    val claimPeriodSeconds: UInt
): Operation() {

//    export const htlc_create = new Serializer("htlc_create", {
//        fee: asset,
//        from: protocol_id_type("account"),
//        to: protocol_id_type("account"),
//        amount: asset,
//        preimage_hash: static_variant([bytes(20), bytes(20), bytes(32)]),
//        preimage_size: uint16,
//        claim_period_seconds: uint32,

    companion object {

        const val KEY_FROM = "from"
        const val KEY_TO = "to"
        const val KEY_AMOUNT = "amount"
        const val KEY_PREIMAGE_HASH = "preimage_hash"
        const val KEY_PREIMAGE_SIZE = "preimage_size"
        const val KEY_CLAIM_PERIOD_SECONDS = "claim_period_seconds"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): HtlcCreateOperation {
            return HtlcCreateOperation(
                rawJson.optGrapheneInstance(KEY_FROM),
                rawJson.optGrapheneInstance(KEY_TO),
                rawJson.optItem(KEY_AMOUNT),
                rawJson.optIterable<String>(KEY_PREIMAGE_HASH).toList(),
                rawJson.optUShort(KEY_PREIMAGE_SIZE),
                rawJson.optUInt(KEY_CLAIM_PERIOD_SECONDS)
            ).apply{
                fee = rawJson.optItem(KEY_FEE)
                result = createGraphene(rawJsonResult.optString(1))
            }
        }
    }

    var result: HtlcObject = createGrapheneEmptyInstance()

    override val operationType = OperationType.HTLC_CREATE_OPERATION

    val expiryTime get() = Date(createTime.time + claimPeriodSeconds.toLong() * 1000)

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}