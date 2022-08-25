package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.VestingPolicyInitializer
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class VestingBalanceCreateOperation(
    var creator: AccountObject,
    var owner: AccountObject,
    var amount: AssetAmount,
    val policy: VestingPolicyInitializer
): Operation() {

    companion object {

//        export const linear_vesting_policy_initializer = new Serializer(
//        "linear_vesting_policy_initializer",
//        {
//            begin_timestamp: time_point_sec,
//            vesting_cliff_seconds: uint32,
//            vesting_duration_seconds: uint32
//        }
//        );
//
//        export const cdd_vesting_policy_initializer = new Serializer(
//        "cdd_vesting_policy_initializer",
//        {
//            start_claim: time_point_sec,
//            vesting_seconds: uint32
//        }
//        );
//
//        var vesting_policy_initializer = static_variant([
//            linear_vesting_policy_initializer,
//            cdd_vesting_policy_initializer
//        ]);
//
//        export const vesting_balance_create = new Serializer("vesting_balance_create", {
//            fee: asset,
//            creator: protocol_id_type("account"),
//            owner: protocol_id_type("account"),
//            amount: asset,
//            policy: vesting_policy_initializer
//        });

        const val KEY_CREATOR = "creator"
        const val KEY_OWNER = "owner"
        const val KEY_AMOUNT = "amount"
        const val KEY_POLICY = "policy"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): VestingBalanceCreateOperation {
            return VestingBalanceCreateOperation(
                rawJson.optGrapheneInstance(KEY_CREATOR),
                rawJson.optGrapheneInstance(KEY_OWNER),
                rawJson.optItem(KEY_AMOUNT),
                rawJson.optItem(KEY_POLICY)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.VESTING_BALANCE_CREATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}