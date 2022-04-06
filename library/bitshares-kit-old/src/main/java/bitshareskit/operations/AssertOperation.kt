package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.Predicate
import bitshareskit.objects.AccountObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AssertOperation(
    var account: AccountObject,
    val predicates: List<Predicate>,
    val requiredAuths: Set<AccountObject>
): Operation() {

//    fee: asset,
//    fee_paying_account: protocol_id_type("account"),
//    predicates: array(predicate),
//    required_auths: set(protocol_id_type("account")),
//    extensions: set(future_extensions)

    companion object {

        const val KEY_FEE_PAYING_ACCOUNT = "fee_paying_account"
        const val KEY_PREDICATES = "predicates"
        const val KEY_REQUIRED_AUTHS = "required_auths"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssertOperation {
            return AssertOperation(
                rawJson.optGrapheneInstance(KEY_FEE_PAYING_ACCOUNT),
                rawJson.optIterable<JSONArray>(KEY_PREDICATES).map { Predicate.fromJsonPair(it) },
                rawJson.optIterable<String>(KEY_REQUIRED_AUTHS).map { createGraphene<AccountObject>(it) }.toSet()
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSERT_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}