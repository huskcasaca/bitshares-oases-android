package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class BidCollateralOperation(
    var bidder: AccountObject,
    var additionalCollateral: AssetAmount,
    var debtCovered: AssetAmount
): Operation() {

    companion object {
//        bidder: protocol_id_type("account"),
//        additional_collateral: asset,
//        debt_covered: asset,

        const val KEY_BIDDER = "bidder"
        const val KEY_ADDITIONAL_COLLATERAL = "additional_collateral"
        const val KEY_DEBT_COVERED = "debt_covered"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): BidCollateralOperation {
            return BidCollateralOperation(
                rawJson.optGrapheneInstance(KEY_BIDDER),
                rawJson.optItem(KEY_ADDITIONAL_COLLATERAL),
                rawJson.optItem(KEY_DEBT_COVERED)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.BID_COLLATERAL_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}