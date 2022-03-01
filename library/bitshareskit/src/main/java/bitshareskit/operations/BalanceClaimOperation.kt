package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.PublicKey
import bitshareskit.objects.AccountObject
import bitshareskit.objects.BalanceObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class BalanceClaimOperation(
    var account: AccountObject,
    val balanceToClaim: BalanceObject,
    val balanceOwnerKey: PublicKey,
    val totalClaimed: AssetAmount
): Operation() {

    companion object {

//        deposit_to_account: protocol_id_type("account"),
//        balance_to_claim: protocol_id_type("balance"),
//        balance_owner_key: public_key,
//        total_claimed: asset

        const val KEY_DEPOSIT_TO_ACCOUNT = "deposit_to_account"
        const val KEY_BALANCE_TO_CLAIM = "balance_to_claim"
        const val KEY_BALANCE_OWNER_KEY = "balance_owner_key"
        const val KEY_TOTAL_CLAIMED = "total_claimed"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): BalanceClaimOperation {
            return BalanceClaimOperation(
                rawJson.optGrapheneInstance(KEY_DEPOSIT_TO_ACCOUNT),
                rawJson.optGrapheneInstance(KEY_BALANCE_TO_CLAIM),
                rawJson.optPublicKey(KEY_BALANCE_OWNER_KEY),
                rawJson.optItem(KEY_TOTAL_CLAIMED
                )
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.BALANCE_CLAIM_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}