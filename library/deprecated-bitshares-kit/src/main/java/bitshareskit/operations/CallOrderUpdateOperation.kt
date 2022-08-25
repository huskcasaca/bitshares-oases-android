package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.Optional
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneIndexedSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class CallOrderUpdateOperation(
    var account: AccountObject,
    var deltaCollateral: AssetAmount,
    var deltaDebt: AssetAmount,
    val targetCollateralRatio: Optional<UShort> = Optional.empty()
): Operation() {

    constructor(
        account: AccountObject,
        deltaCollateral: AssetAmount,
        deltaDebt: AssetAmount,
        targetCollateralRatio: UShort?
    ) : this(account, deltaCollateral, deltaDebt, Optional(targetCollateralRatio))

    init {
        if (targetCollateralRatio.isPresent) extensions.add(targetCollateralRatio.fieldSafe)
    }

    companion object {
        const val KEY_FUNDING_ACCOUNT = "funding_account"
        const val KEY_DELTA_COLLATERAL = "delta_collateral"
        const val KEY_DELTA_DEBT = "delta_debt"
        const val KEY_TARGET_COLLATERAL_RATIO = "target_collateral_ratio"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): CallOrderUpdateOperation {
            return CallOrderUpdateOperation(
                rawJson.optGrapheneInstance(KEY_FUNDING_ACCOUNT),
                rawJson.optItem(KEY_DELTA_COLLATERAL),
                rawJson.optItem(KEY_DELTA_DEBT),
                Optional.empty()
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.CALL_ORDER_UPDATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeSerializable(account)
        writeSerializable(deltaCollateral)
        writeSerializable(deltaDebt)
        writeGrapheneIndexedSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject {
        return buildJsonObject {
            putSerializable(KEY_FEE, fee)
            putSerializable(KEY_FUNDING_ACCOUNT, account)
            putSerializable(KEY_DELTA_COLLATERAL, deltaCollateral)
            putSerializable(KEY_DELTA_DEBT, deltaDebt)
            putJsonObject(KEY_EXTENSIONS) {
                putSerializable(KEY_TARGET_COLLATERAL_RATIO, targetCollateralRatio)
            }
        }
    }



}