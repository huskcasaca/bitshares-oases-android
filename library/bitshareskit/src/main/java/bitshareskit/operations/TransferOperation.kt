package bitshareskit.operations

import bitshareskit.ks_chain.Authority
import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.Memo
import bitshareskit.models.Optional
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class TransferOperation(
    var from: AccountObject,
    var to: AccountObject,
    var amount: AssetAmount,
    val memo: Optional<Memo>
): Operation() {

    constructor(from: AccountObject, to: AccountObject, amount: AssetAmount, memo: Memo?): this (from, to, amount, Optional.from(memo))

    companion object {
        const val KEY_FROM = "from"
        const val KEY_TO = "to"
        const val KEY_AMOUNT = "amount"
        const val KEY_MEMO = "memo"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): TransferOperation {
            return TransferOperation(
                rawJson.optGrapheneInstance(KEY_FROM),
                rawJson.optGrapheneInstance(KEY_TO),
                rawJson.optItem(KEY_AMOUNT),
                rawJson.optOptionalItem(KEY_MEMO)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.TRANSFER_OPERATION

    override val authority: Authority = Authority.ACTIVE

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeSerializable(from)
        writeSerializable(to)
        writeSerializable(amount)
        writeSerializable(memo)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putSerializable(KEY_FROM, from)
        putSerializable(KEY_TO, to)
        putSerializable(KEY_AMOUNT, amount)
        putSerializable(KEY_MEMO, memo)
        putArray(KEY_EXTENSIONS, extensions)
    }
}